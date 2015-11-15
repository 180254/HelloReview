package pl.p.lodz.iis.hr.services;


import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import pl.p.lodz.iis.hr.configuration.appconfig.AppConfig;
import pl.p.lodz.iis.hr.configuration.appconfig.GitHubDummy;
import pl.p.lodz.iis.hr.exceptions.GitHubCommunicationException;
import pl.p.lodz.iis.hr.models.courses.Commission;
import pl.p.lodz.iis.hr.models.courses.CommissionStatus;
import pl.p.lodz.iis.hr.repositories.CommissionRepository;
import pl.p.lodz.iis.hr.utils.ExceptionUtil;
import pl.p.lodz.iis.hr.utils.GitHubExecutor;

import java.io.File;
import java.io.IOException;
import java.util.Set;

class GitExecuteCloneTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitExecuteCloneTask.class);

    private final String intNo;
    private final Commission commission;
    private final GHRepository assessedRepo;

    private GitHub gitHubWait;
    private CredentialsProvider jGitCredentials;
    private CommissionRepository commissionRepository;
    private GitHubDummy dummy;

    private String targetRepoName;
    private String targetRepoFullName;

    private File directoryForClone;
    private File directoryForCloneGitSubdir;

    private Set<String> assessedRepoBranches;
    private GHRepository targetRepo;

    GitExecuteCloneTask(Commission commission, GHRepository assessedRepo) {

        intNo = commission.getUuid().toString();
        this.commission = commission;
        this.assessedRepo = assessedRepo;

        LOGGER.info("{} Repo cloning scheduled for review: {}, assessed: {}, assessor {}.",
                intNo,
                commission.getReview().getName(),
                commission.getAssessed().getName(),
                commission.getAssessor().getName()
        );
    }

    private void init() {
        LOGGER.debug("{} Dependencies init.", intNo);

        AppConfig appConfig = ApplicationContextProvider.getBean(AppConfig.class);
        gitHubWait = ApplicationContextProvider.getBean("gitHubWait", GitHub.class);
        jGitCredentials = ApplicationContextProvider.getBean(CredentialsProvider.class);
        commissionRepository = ApplicationContextProvider.getBean(CommissionRepository.class);

        LOGGER.debug("{} Paths init.", intNo);

        dummy = appConfig.getGitHubConfig().getDummy();
        targetRepoName = commission.getUuid().toString();
        targetRepoFullName = String.format("%s/%s", dummy.getUsername(), targetRepoName);

        String temporaryDir = appConfig.getGeneralConfig().getTempDir();
        String dirForClonePath = String.format("%s%s%s", temporaryDir, File.separator, targetRepoName);
        String dirForCloneGitSP = String.format("%s%s.git", dirForClonePath, File.separator);
        directoryForClone = new File(dirForClonePath);
        directoryForCloneGitSubdir = new File(dirForCloneGitSP);
    }

    @Override
    @Transactional
    public void run() {
        LOGGER.debug("{} Repo cloning started.", intNo);

        init();

        try {
            deleteDirForCloneIfExist(1);
            deleteTargetRepoIfExist();
            getListOfBranchesOfAssessedRepo();
            createTargetRepo();

            for (String branch : assessedRepoBranches) {
                LOGGER.debug("{} Processing branch: {}", intNo, branch);

                createDirForClone();
                cloneAssessedRepo(branch);
                removeGitSubdirInClone();

                LOGGER.debug("{} Init-ing clone dir as new repo.", intNo);

                try (Git gitTarget = Git.init().setDirectory(directoryForClone).call()) {

                    LOGGER.debug("{} Init-ed clone dir as new repo.", intNo);

                    addAllFilesToCommit(gitTarget);
                    setRepoConfiguration(gitTarget);
                    commitFiles(gitTarget);
                    createBranchIfRequired(gitTarget, branch);
                    pushChangesIntoTargetRepo(gitTarget);
                }

                deleteDirForCloneIfExist(2);
            }

            setDefaultBranchSameAsInAssessed();

            LOGGER.debug("{} Repo cloning done. Updating commision status.", intNo);

            commission.setStatus(CommissionStatus.NOT_FILLED);
            commission.setGhUrl(targetRepo.getHtmlUrl().toString());
            commissionRepository.save(commission);

            LOGGER.debug("{} Repo cloning done. Updated commision status.", intNo);
            LOGGER.info("{} Done.", intNo);

        } catch (GitAPIException | GitHubCommunicationException | IOException e) {
            LOGGER.info("{} Repo cloning failed.", intNo, e);

            LOGGER.debug("{} Cleaning. Deleting clone dir if exist.", intNo);
            ExceptionUtil.ignoreException2(() -> deleteDirForCloneIfExist(2));

            LOGGER.debug("{} Cleaning. Deleting target repo if exist.", intNo);
            ExceptionUtil.ignoreException2(() -> GitHubExecutor.ex(
                    () -> gitHubWait.getRepository(targetRepoFullName).delete()
            ));

            LOGGER.debug("{} Cleaning. Updating commision status.", intNo);
            commission.setStatus(CommissionStatus.PROCESSING_FAILED);
            commissionRepository.save(commission);

            LOGGER.debug("{} Cleaning done. ", intNo);
            LOGGER.info("{} Done.", intNo);
        }

    }


    private void deleteDirForCloneIfExist(int cause) throws IOException {
        boolean exists = directoryForClone.exists();

        if (cause == 1) {
            LOGGER.debug("{} Directory for clone should not exist, currently exist = {}", intNo, exists);
        } else if (cause == 2) {
            LOGGER.debug("{} Cleaning. Directory for clone should exist, currently exist = {}", intNo, exists);

        }

        if (exists) {
            FileUtils.deleteDirectory(directoryForClone);
            LOGGER.debug("{} Directory for clone deleted.", intNo);
        }
    }

    private void deleteTargetRepoIfExist() throws GitHubCommunicationException {
        LOGGER.debug("{} Checking if target repo exist.", intNo);

        boolean targetRepoExist = GitHubExecutor.ex(() ->
                gitHubWait.searchRepositories()
                        .user(dummy.getUsername()).list().asList()
                        .stream().map(GHRepository::getName)
                        .anyMatch(name -> name.equals(targetRepoName)));

        LOGGER.debug("{} Target repo should not exist, currently  = {}", intNo, targetRepoExist);

        if (targetRepoExist) {
            GitHubExecutor.ex(() -> gitHubWait.getRepository(targetRepoFullName).delete());
            LOGGER.debug("{} Target repo deleted.", intNo);
        }
    }

    private void getListOfBranchesOfAssessedRepo() throws GitHubCommunicationException {
        LOGGER.debug("{} Retrieving list of branches of assessed repo.", intNo);

        assessedRepoBranches = GitHubExecutor.ex(() -> assessedRepo.getBranches().keySet());

        LOGGER.debug("{} Retrieved list of branches of assessed repo.", intNo);
        LOGGER.debug("{} {} branch found.", intNo, assessedRepoBranches.size());

        int i = 0;
        for (String assessedRepoBranch : assessedRepoBranches) {
            LOGGER.trace("{} {}. {}", intNo, i, assessedRepoBranch);
            i++;
        }

    }

    private void createTargetRepo() throws GitHubCommunicationException {
        LOGGER.debug("{} Creating target repo.", intNo);

        targetRepo = GitHubExecutor.ex(() ->
                gitHubWait.createRepository(targetRepoName, dummy.getCommitMsg(), null, true));

        LOGGER.debug("{} Created target repo.", intNo);
    }


    private void createDirForClone() throws IOException {
        LOGGER.debug("{} Creating directory for clone.", intNo);

        if (!directoryForClone.mkdir()) {
            throw new IOException("Unable to create directory for clone.");
        }

        LOGGER.debug("{} Created directory for clone.", intNo);

    }

    private void cloneAssessedRepo(String branch) throws GitAPIException {
        LOGGER.debug("{} Cloning assessed repo, branch: {}", intNo, branch);

        try (Git gitClone = Git.cloneRepository()
                .setURI(assessedRepo.gitHttpTransportUrl())
                .setDirectory(directoryForClone)
                .setBranch(branch)
                .setCredentialsProvider(jGitCredentials)
                .call()) {
        }

        LOGGER.debug("{} Cloned assessed repo, branch: {}", intNo, branch);

    }

    private void removeGitSubdirInClone() throws IOException {
        LOGGER.debug("{} Removing .git subdir in clone.", intNo);

        FileUtils.deleteDirectory(directoryForCloneGitSubdir);

        LOGGER.debug("{} Removed .git subdir in clone.", intNo);
    }

    private void addAllFilesToCommit(Git gitTarget) throws GitAPIException {
        LOGGER.debug("{} Adding all files to commit.", intNo);

        int i = 0;
        AddCommand add = gitTarget.add();
        String[] files = directoryForClone.list();

        for (String fileName : files) {
            LOGGER.trace("{}. {}", i, fileName);
            add.addFilepattern(fileName);
            i++;
        }

        add.call();

        LOGGER.debug("{} Added all files to commit.", intNo);
    }

    private void setRepoConfiguration(Git gitTarget) throws IOException {
        LOGGER.debug("{} Setting repo configuration.", intNo);

        StoredConfig gitTargetConfig = gitTarget.getRepository().getConfig();
        gitTargetConfig.setString("user", null, "name", dummy.getName());
        gitTargetConfig.setString("user", null, "email", dummy.getEmail());
        gitTargetConfig.setBoolean("core", null, "autocrlf", true);
        gitTargetConfig.save();

        LOGGER.debug("{} Set repo configuration.", intNo);
    }

    private void commitFiles(Git gitTarget) throws GitAPIException {
        LOGGER.debug("{} Committing files.", intNo);

        gitTarget.commit().setMessage(dummy.getCommitMsg()).call();

        LOGGER.debug("{} Committed files.", intNo);
    }


    private void createBranchIfRequired(Git gitTarget, String branch) throws GitAPIException {

        boolean cratingRequired = !branch.equals("master");
        LOGGER.debug("{} Current branch: {}, creating required: {}", intNo, branch, cratingRequired);

        if (cratingRequired) {
            LOGGER.debug("{} Creating branch.", intNo);

            gitTarget.branchCreate().setName(branch).call();
            gitTarget.checkout().setName(branch).call();

            LOGGER.debug("{} Created branch.", intNo);
        }
    }

    private void pushChangesIntoTargetRepo(Git gitTarget) throws GitAPIException {
        LOGGER.debug("{} Pushing branch.", intNo);

        gitTarget.push()
                .setRemote(targetRepo.gitHttpTransportUrl())
                .setCredentialsProvider(jGitCredentials)
                .call();

        LOGGER.debug("{} Pushed branch.", intNo);
    }

    private void setDefaultBranchSameAsInAssessed() throws GitHubCommunicationException {
        String defaultBranch = assessedRepo.getDefaultBranch();
        LOGGER.debug("{} Setting default branch to {}", intNo, defaultBranch);

        GitHubExecutor.ex(() -> targetRepo.setDefaultBranch(defaultBranch));

        LOGGER.debug("{} Set default branch.", intNo);
    }

}
