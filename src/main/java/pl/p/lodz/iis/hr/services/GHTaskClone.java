package pl.p.lodz.iis.hr.services;

import com.jayway.awaitility.Awaitility;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import pl.p.lodz.iis.hr.appconfig.AppConfig;
import pl.p.lodz.iis.hr.appconfig.GHDummy;
import pl.p.lodz.iis.hr.exceptions.GHCommunicationException;
import pl.p.lodz.iis.hr.models.courses.Commission;
import pl.p.lodz.iis.hr.models.courses.CommissionStatus;
import pl.p.lodz.iis.hr.repositories.CommissionRepository;
import pl.p.lodz.iis.hr.utils.ExceptionUtils;
import pl.p.lodz.iis.hr.utils.GHExecutor;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Repository cloner. Each repository must be anonymously cloned, as peer should not known who is assessed.<br/>
 * Whole Clone logic is covered by this class.
 */
class GHTaskClone implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GHTaskClone.class);
    private static final Pattern GH_URL = Pattern.compile("https?://github.com/");

    private final String uuid;
    private final Commission commission;

    private GHTaskDirRemove ghTaskDirRemove;
    private GitHub ghWait;
    private CredentialsProvider jGitCredentials;
    private CommissionRepository commissionRepository;

    private GHDummy ghDummy;
    private GHMyself ghMyself;

    private GHRepository assessedRepo;
    private String targetRepoSimpleName;
    private String targetRepoFullName;

    private File directoryForClone;
    private File directoryForCloneGitSubdir;

    private Set<String> assessedRepoBranches;
    private GHRepository targetRepo;

    GHTaskClone(Commission commission) {

        uuid = commission.getUuid().toString();
        this.commission = commission;

        LOGGER.info("{} Repo cloning scheduled for review: {}, assessed: {}, assessor {}.",
                uuid,
                commission.getReview().getName(),
                commission.getAssessed().getName(),
                commission.getAssessor().getName()
        );
    }

    private void init() throws GHCommunicationException {
        LOGGER.debug("{} Dependencies init.", uuid);

        AppConfig appConfig = ApplicationContextProvider.getBean(AppConfig.class);
        ghTaskDirRemove = ApplicationContextProvider.getBean(GHTaskDirRemove.class);
        ghWait = ApplicationContextProvider.getBean("ghWait", GitHub.class);
        jGitCredentials = ApplicationContextProvider.getBean(CredentialsProvider.class);
        commissionRepository = ApplicationContextProvider.getBean(CommissionRepository.class);

        LOGGER.debug("{} Paths init.", uuid);

        ghDummy = appConfig.getGitHubConfig().getDummy();
        ghMyself = GHExecutor.ex(() -> ghWait.getMyself());

        String assessedRepoFullName = GH_URL.matcher(commission.getAssessedGhUrl()).replaceAll("");
        assessedRepo = GHExecutor.ex(() -> ghWait.getRepository(assessedRepoFullName));
        targetRepoSimpleName = commission.getUuid().toString();
        targetRepoFullName = String.format("%s/%s", ghMyself.getLogin(), targetRepoSimpleName);

        Random random = new SecureRandom();
        String tempDir = appConfig.getGeneralConfig().getTempDir();
        String dirForClonePath = String.format("%s%s%s%d", tempDir, File.separator, targetRepoSimpleName, random.nextInt());
        String dirForCloneGitSP = String.format("%s%s.git", dirForClonePath, File.separator);
        directoryForClone = new File(dirForClonePath);
        directoryForCloneGitSubdir = new File(dirForCloneGitSP);
    }

    @Override
    @Transactional
    public void run() {
        LOGGER.debug("{} Repo cloning started.", uuid);

        try {
            init();

            deleteDirForCloneIfExist(false);
            deleteTargetRepoIfExist();
            getListOfBranchesOfAssessedRepo();
            createTargetRepo();

            for (String branch : assessedRepoBranches) {
                LOGGER.debug("{} Processing branch: {}", uuid, branch);

                createDirForClone();
                cloneAssessedRepo(branch);
                removeGitSubdirInClone();

                LOGGER.debug("{} Init-ing clone dir as new repo.", uuid);

                try (Git gitTarget = Git.init().setDirectory(directoryForClone).call()) {

                    LOGGER.debug("{} Init-ed clone dir as new repo.", uuid);

                    addAllFilesToCommit(gitTarget);
                    setRepoConfiguration(gitTarget);
                    commitFiles(gitTarget);
                    createBranchIfRequired(gitTarget, branch);
                    pushChangesIntoTargetRepo(gitTarget);
                    awaitUntilPushIsCompleted(branch);
                }

                deleteDirForCloneIfExist(true);
            }

            setDefaultBranchSameAsInAssessed();

            LOGGER.debug("{} Repo cloning done. Updating commision status.", uuid);

            commission.setStatus(CommissionStatus.UNFILLED);
            commission.setGhUrl(targetRepo.getHtmlUrl().toString());
            commissionRepository.save(commission);

            LOGGER.debug("{} Repo cloning done. Updated commision status.", uuid);
            LOGGER.info("{} Done.", uuid);

        } catch (GitAPIException | GHCommunicationException | IOException | RuntimeException e) {
            LOGGER.info("{} Repo cloning failed.", uuid, e);

            LOGGER.debug("{} Cleaning. Updating commission status.", uuid);
            commission.setStatus(CommissionStatus.PROCESSING_FAILED);
            commissionRepository.save(commission);

            LOGGER.debug("{} Cleaning. Deleting target repo if exist.", uuid);
            ExceptionUtils.ignoreException2(() ->
                    GHExecutor.ex(() -> ghWait.getRepository(targetRepoFullName).delete())
            );

            LOGGER.debug("{} Cleaning. Deleting clone dir if exist.", uuid);
            ExceptionUtils.ignoreException2(() ->
                    deleteDirForCloneIfExist(false)
            );

            LOGGER.debug("{} Cleaning done. ", uuid);
            LOGGER.info("{} Done.", uuid);
        }

    }

    private void deleteDirForCloneIfExist(boolean shouldExist) throws IOException {
        boolean exists = directoryForClone.exists();

        LOGGER.debug("{} Directory for clone should {}, currently exist = {}",
                uuid, shouldExist ? "exist" : "not exist", exists
        );

        if (exists) {
            ghTaskDirRemove.deleteDirectoryNIOWait(directoryForClone.toPath());
            LOGGER.debug("{} Directory for clone deleted.", uuid);
        }
    }

    private void deleteTargetRepoIfExist() throws GHCommunicationException {
        LOGGER.debug("{} Checking if target repo exist.", uuid);

        boolean targetRepoExist = GHExecutor.ex(() ->
                ghWait.getMyself().getRepositories().keySet().contains(targetRepoSimpleName)
        );

        LOGGER.debug("{} Target repo should not exist, currently  = {}", uuid, targetRepoExist);

        if (targetRepoExist) {
            GHExecutor.ex(() -> ghWait.getRepository(targetRepoFullName).delete());
            LOGGER.debug("{} Target repo deleted.", uuid);
        }
    }

    private void getListOfBranchesOfAssessedRepo() throws GHCommunicationException {
        LOGGER.debug("{} Retrieving list of branches of assessed repo.", uuid);

        assessedRepoBranches = GHExecutor.ex(() -> assessedRepo.getBranches().keySet());
        String branches = assessedRepoBranches.stream().reduce((b1, b2) -> String.format("%s, %s", b1, b2)).orElse("");

        LOGGER.debug("{} Retrieved list of branches of assessed repo.", uuid);
        LOGGER.debug("{} {} branch found: {}", uuid, assessedRepoBranches.size(), branches);
    }

    private void createTargetRepo() throws GHCommunicationException {
        LOGGER.debug("{} Creating target repo.", uuid);

        targetRepo = GHExecutor.ex(() ->
                ghWait.createRepository(targetRepoSimpleName, ghDummy.getCommitMsg(), null, true)
        );

        LOGGER.debug("{} Created target repo.", uuid);
    }


    private void createDirForClone() throws IOException {
        LOGGER.debug("{} Creating directory for clone.", uuid);

        if (!directoryForClone.mkdir()) {
            throw new IOException("Unable to create directory for clone.");
        }

        LOGGER.debug("{} Created directory for clone.", uuid);
    }

    private void cloneAssessedRepo(String branch) throws GitAPIException {
        LOGGER.debug("{} Cloning assessed repo, branch: {}", uuid, branch);

        try (Git gitClone = Git.cloneRepository()
                .setURI(assessedRepo.gitHttpTransportUrl())
                .setDirectory(directoryForClone)
                .setBranch(branch)
                .setCredentialsProvider(jGitCredentials)
                .call()) {
        }

        LOGGER.debug("{} Cloned assessed repo, branch: {}", uuid, branch);
    }

    private void removeGitSubdirInClone() throws IOException {
        LOGGER.debug("{} Removing .git subdir in clone.", uuid);

        ghTaskDirRemove.deleteDirectoryNIOWait(directoryForCloneGitSubdir.toPath());

        LOGGER.debug("{} Removed .git subdir in clone.", uuid);
    }

    private void addAllFilesToCommit(Git gitTarget) throws GitAPIException {
        LOGGER.debug("{} Adding all files to commit.", uuid);

        AddCommand add = gitTarget.add();
        String[] files = directoryForClone.list();

        int i = 0;
        for (String fileName : files) {
            LOGGER.trace("{}. {}", i, fileName);
            add.addFilepattern(fileName);
            i++;
        }

        add.call();

        LOGGER.debug("{} Added all files to commit.", uuid);
    }

    private void setRepoConfiguration(Git gitTarget) throws IOException, GHCommunicationException {
        LOGGER.debug("{} Setting repo configuration.", uuid);

        StoredConfig gitTargetConfig = gitTarget.getRepository().getConfig();
        gitTargetConfig.setString("user", null, "name", GHExecutor.ex(() -> ghMyself.getName()));
        gitTargetConfig.setString("user", null, "email", GHExecutor.ex(() -> ghMyself.getEmails2().get(0).getEmail()));
        gitTargetConfig.setBoolean("core", null, "autocrlf", true);
        gitTargetConfig.save();

        LOGGER.debug("{} Set repo configuration.", uuid);
    }

    private void commitFiles(Git gitTarget) throws GitAPIException {
        LOGGER.debug("{} Committing files.", uuid);

        gitTarget.commit().setMessage(ghDummy.getCommitMsg()).call();

        LOGGER.debug("{} Committed files.", uuid);
    }


    private void createBranchIfRequired(Git gitTarget, String branch) throws GitAPIException {

        boolean cratingRequired = !branch.equals("master");
        LOGGER.debug("{} Current branch: {}, creating required: {}", uuid, branch, cratingRequired);

        if (cratingRequired) {
            LOGGER.debug("{} Creating branch.", uuid);

            gitTarget.branchCreate().setName(branch).call();
            gitTarget.checkout().setName(branch).call();

            LOGGER.debug("{} Created branch.", uuid);
        }
    }

    private void pushChangesIntoTargetRepo(Git gitTarget) throws GitAPIException {
        LOGGER.debug("{} Pushing branch.", uuid);

        gitTarget.push()
                .setRemote(targetRepo.gitHttpTransportUrl())
                .setCredentialsProvider(jGitCredentials)
                .call();

        LOGGER.debug("{} Pushed branch.", uuid);
    }

    private void awaitUntilPushIsCompleted(String branch) throws IOException {
        LOGGER.debug("{} Awaiting until push completed.", uuid);

        try {

            Awaitility.await("push is visible by GitHub api")
                    .atMost(2L, TimeUnit.MINUTES)
                    .pollDelay(3L, TimeUnit.SECONDS)
                    .pollInterval(3L, TimeUnit.SECONDS)
                    .until(() -> GHExecutor.ex(() -> targetRepo.getBranches().keySet().contains(branch)));

        } catch (Throwable ex) {
            throw new IOException(ex);
        }

        LOGGER.debug("{} Awaiting push finish done..", uuid);
    }

    private void setDefaultBranchSameAsInAssessed() throws GHCommunicationException {
        String defaultBranch = assessedRepo.getDefaultBranch();
        LOGGER.debug("{} Setting default branch to {}", uuid, defaultBranch);

        GHExecutor.ex(() -> targetRepo.setDefaultBranch(defaultBranch));

        LOGGER.debug("{} Set default branch.", uuid);
    }
}
