package pl.p.lodz.iis.hr.services;


import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import pl.p.lodz.iis.hr.configuration.appconfig.AppConfig;
import pl.p.lodz.iis.hr.configuration.appconfig.GitHubDummy;
import pl.p.lodz.iis.hr.exceptions.GitHubCommunicationException;
import pl.p.lodz.iis.hr.models.response.ReviewResponse;
import pl.p.lodz.iis.hr.models.response.ReviewResponseStatus;
import pl.p.lodz.iis.hr.repositories.ReviewResponseRepository;
import pl.p.lodz.iis.hr.utils.ExceptionChecker;
import pl.p.lodz.iis.hr.utils.GitHubExecutor;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static pl.p.lodz.iis.hr.utils.ExceptionChecker.exceptionThrown2;

public class GitCloneTask implements Runnable {

    private static final String COMMIT_MSG = "clone for peer review purposes";

    private AppConfig appConfig;
    private GitHub gitHub;
    private CredentialsProvider jGitCredentials;
    private ReviewResponseRepository responseRepository;
    private ReviewResponse response;
    private GHRepository assessedRepository;

    public GitCloneTask(AppConfig appConfig,
                        GitHub gitHub,
                        CredentialsProvider jGitCredentials,
                        ReviewResponseRepository responseRepository,
                        ReviewResponse response,
                        GHRepository assessedRepository) {

        this.appConfig = appConfig;
        this.gitHub = gitHub;
        this.jGitCredentials = jGitCredentials;
        this.responseRepository = responseRepository;
        this.response = response;
        this.assessedRepository = assessedRepository;
    }

    @Override
    public void run() {
        GitHubDummy dummy = appConfig.getGitHubConfig().getDummy();
        String dummyUsername = dummy.getUsername();
        String tempDir = appConfig.getGeneralConfig().getTempDir();

        String targetName = response.getUuid().toString();
        String targetRepoName = String.format("%s/%s", dummyUsername, targetName);

        String clonePath = String.format("%s%s%s", tempDir, File.separator, targetName);
        String clonePathSubGit = String.format("%s%s.git", clonePath, File.separator);

        File cloneDir = new File(clonePath);
        File cloneDirSubGit = new File(clonePathSubGit);

        Set<String> branches;
        GHRepository targetRepo;

        try {

            boolean targetRepoExist = GitHubExecutor.ex(() ->
                    gitHub.searchRepositories().user(dummyUsername)
                            .list().asList()
                            .stream().map(GHRepository::getName)).anyMatch(name -> name.equals(targetName));

            if (targetRepoExist) {
                GitHubExecutor.ex(() -> gitHub.getRepository(targetRepoName).delete());
            }

            branches = GitHubExecutor.ex(() -> assessedRepository.getBranches().keySet());
            targetRepo = GitHubExecutor.ex(() -> gitHub.createRepository(targetName, COMMIT_MSG, COMMIT_MSG, true));

        } catch (GitHubCommunicationException ignored) {
            response.setStatus(ReviewResponseStatus.PROCESSING_EROR);
            responseRepository.save(response);
            return;
        }

        if (cloneDir.exists()) {
            if (exceptionThrown2(() -> FileUtils.deleteDirectory(cloneDir))) {
                response.setStatus(ReviewResponseStatus.PROCESSING_EROR);
                responseRepository.save(response);
                return;
            }
        }

        try {

            for (String branch : branches) {

                if (!cloneDir.mkdir()) {
                    throw new IOException("Unable to create clone dir");
                }

                try (Git gitClone = Git.cloneRepository()
                        .setURI(assessedRepository.gitHttpTransportUrl())
                        .setDirectory(cloneDir)
                        .setBranch(branch)
                        .setCredentialsProvider(jGitCredentials)
                        .call()) {
                }

                FileUtils.deleteDirectory(cloneDirSubGit);

                try (Git gitTarget = Git.init()
                        .setDirectory(cloneDir)
                        .call()) {

                    AddCommand add = gitTarget.add();
                    for (String fileName : cloneDir.list()) {
                        add.addFilepattern(fileName);
                    }
                    add.call();

                    StoredConfig gitTargetConfig = gitTarget.getRepository().getConfig();
                    gitTargetConfig.setString("user", null, "name", "Peer Review 70");
                    gitTargetConfig.setString("user", null, "email", "peerreview70@gmail.com");
                    gitTargetConfig.setBoolean("core", null, "autocrlf", true);
                    gitTargetConfig.save();

                    gitTarget.commit().setMessage(COMMIT_MSG).call();

                    if (!branch.equals("master")) {
                        gitTarget.branchCreate().setName(branch).call();
                        gitTarget.checkout().setName(branch).call();
                    }

                    gitTarget.push()
                            .setRemote(targetRepo.gitHttpTransportUrl())
                            .setCredentialsProvider(jGitCredentials)
                            .call();
                }

                FileUtils.deleteDirectory(cloneDir);

            }

            response.setStatus(ReviewResponseStatus.NOT_FILLED);
            response.setGhUrl(targetRepo.getHtmlUrl().toString());
            responseRepository.save(response);

        } catch (GitAPIException | IOException ignored) {

            if (cloneDir.exists()) {
                cloneDir.delete();
            }

            ExceptionChecker.ignoreException2(() ->
                    GitHubExecutor.ex(() -> gitHub.getRepository(targetRepoName).delete()));

            response.setStatus(ReviewResponseStatus.PROCESSING_EROR);
            responseRepository.save(response);
        }

    }
}
