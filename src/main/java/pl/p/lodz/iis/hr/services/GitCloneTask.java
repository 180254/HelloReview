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
import pl.p.lodz.iis.hr.models.response.ReviewResponse;
import pl.p.lodz.iis.hr.models.response.ReviewResponseStatus;
import pl.p.lodz.iis.hr.repositories.ReviewResponseRepository;
import pl.p.lodz.iis.hr.utils.ExceptionUtil;
import pl.p.lodz.iis.hr.utils.GitHubExecutor;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class GitCloneTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitCloneTask.class);

    private final String intNo;

    private final AppConfig appConfig;
    private final GitHub gitHub;
    private final CredentialsProvider jGitCredentials;
    private final ReviewResponseRepository responseRepository;

    private final ReviewResponse response;
    private final GHRepository assessedRepo;

    private GitHubDummy dummy;

    private String targetRepoName;
    private String targetRepoFullName;

    private File directoryForClone;
    private File directoryForCloneGitSubdir;

    private Set<String> assessedRepoBranches;
    private GHRepository targetRepo;

    public GitCloneTask(AppConfig appConfig,
                        GitHub gitHub,
                        CredentialsProvider jGitCredentials,
                        ReviewResponseRepository responseRepository,

                        ReviewResponse response,
                        GHRepository assessedRepo) {

        this.appConfig = appConfig;
        this.gitHub = gitHub;
        this.jGitCredentials = jGitCredentials;
        this.responseRepository = responseRepository;

        this.response = response;
        this.assessedRepo = assessedRepo;

        intNo = response.getUuid().toString();
        LOGGER.info("{} Repo cloning scheduled for review: {}, assessed: {}, assessor {}.",
                intNo,
                response.getReview().getName(),
                response.getAssessed().getName(),
                response.getAssessor().getName()
        );
    }

    private void init() {
        LOGGER.debug("Paths init.");

        dummy = appConfig.getGitHubConfig().getDummy();
        targetRepoName = response.getUuid().toString();
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

                    setDefaultBranchSameAsInAssessed();
                }

                deleteDirForCloneIfExist(2);

            }

            LOGGER.debug("{} Repo cloning done. Updating review response status.", intNo);

            response.setStatus(ReviewResponseStatus.NOT_FILLED);
            response.setGhUrl(targetRepo.getHtmlUrl().toString());
            responseRepository.save(response);

            LOGGER.debug("{} Repo cloning done. Updated review response status.", intNo);
            LOGGER.info("{} Done.", intNo);

        } catch (GitAPIException | GitHubCommunicationException | IOException e) {
            LOGGER.info("{} Repo cloning failed.", intNo, e);

            LOGGER.debug("{} Cleaning. Deleting clone dir if exist.", intNo);
            ExceptionUtil.ignoreException2(() -> deleteDirForCloneIfExist(2));

            LOGGER.debug("{} Cleaning. Deleting target repo if exist.", intNo);
            ExceptionUtil.ignoreException2(() -> GitHubExecutor.ex(
                    () -> gitHub.getRepository(targetRepoFullName).delete()
            ));

            LOGGER.debug("{} Cleaning. Updating review response status.", intNo);
            response.setStatus(ReviewResponseStatus.PROCESSING_EROR);
            responseRepository.save(response);

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
                gitHub.searchRepositories()
                        .user(dummy.getUsername()).list().asList()
                        .stream().map(GHRepository::getName)
                        .anyMatch(name -> name.equals(targetRepoName)));

        LOGGER.debug("{} Target repo should not exist, currently  = {}", intNo, targetRepoExist);

        if (targetRepoExist) {
            GitHubExecutor.ex(() -> gitHub.getRepository(targetRepoFullName).delete());
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
                gitHub.createRepository(targetRepoName, dummy.getCommitMsg(), null, true));

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

        gitTarget.commit().setMessage(dummy.getUsername()).call();

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
        String defaultBranch = targetRepo.getDefaultBranch();
        LOGGER.debug("{} Setting default branch to {}", intNo, defaultBranch);

        GitHubExecutor.ex(() -> targetRepo.setDefaultBranch(defaultBranch));

        LOGGER.debug("{} Set default branch.", intNo);
    }

}
