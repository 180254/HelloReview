package pl.p.lodz.iis.hr.services;


import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.GHRepository;
import pl.p.lodz.iis.hr.configuration.appconfig.AppConfig;
import pl.p.lodz.iis.hr.configuration.appconfig.GitHubDummy;
import pl.p.lodz.iis.hr.exceptions.GitHubCommunicationException;
import pl.p.lodz.iis.hr.models.response.ReviewResponse;
import pl.p.lodz.iis.hr.models.response.ReviewResponseStatus;
import pl.p.lodz.iis.hr.repositories.ReviewResponseRepository;
import pl.p.lodz.iis.hr.utils.GitHubExecutor;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class GitCloneTask implements Runnable {

    private static final String COMMIT_MSG = "clone for peer review purposes";

    private AppConfig appConfig;
    private ReviewResponseRepository responseRepository;
    private ReviewResponse response;
    private GHRepository ghRepository;

    public GitCloneTask(AppConfig appConfig, ReviewResponseRepository responseRepository,
                        ReviewResponse response, GHRepository ghRepository) {

        this.appConfig = appConfig;
        this.responseRepository = responseRepository;
        this.response = response;
        this.ghRepository = ghRepository;
    }

    @Override
    public void run() {
        String assessedUrl;
        Set<String> branches;

        try {
            assessedUrl = GitHubExecutor.ex(() -> ghRepository.getUrl().toString());
            branches = GitHubExecutor.ex(() -> ghRepository.getBranches().keySet());
        } catch (GitHubCommunicationException ignored) {
            response.setStatus(ReviewResponseStatus.PROCESSING_EROR);
            return;
        }
        String cloneID = response.getUuid().toString();
        String clonePath = String.format("%s%s%s", appConfig.getGeneralConfig().getTempDir(), File.separator, cloneID);
        String clonePathGit = String.format("%s%s.git", clonePath, File.separator);

        File cloneDir = new File(clonePath);
        File cloneGitDir = new File(clonePathGit);

        if (!cloneDir.mkdir()) {
            response.setStatus(ReviewResponseStatus.PROCESSING_EROR);
            return;
        }

        try {
            GitHubDummy dummy = appConfig.getGitHubConfig().getDummy();
            CredentialsProvider credentials =
                    new UsernamePasswordCredentialsProvider(dummy.getUsername(), dummy.getPassword());

            for (String branch : branches) {

                try (Git gitClone = Git.cloneRepository()
                        .setURI(assessedUrl)
                        .setDirectory(cloneDir)
                        .setBranch(branch)
                        .setCredentialsProvider(credentials)
                        .call()) {
                }

                FileUtils.deleteDirectory(cloneGitDir);

                try (Git gitNew = Git.init()
                        .setDirectory(cloneDir)
                        .call()) {

                    AddCommand add = gitNew.add();
                    for (String fileName : cloneDir.list()) {
                        add.addFilepattern(fileName);
                    }
                    add.call();

                    gitNew.commit().setMessage(COMMIT_MSG).call();

                    if (!branch.equals("master")) {
                        gitNew.branchCreate().setName(branch).call();
                    }

                    gitNew.push()
                            .setRemote("https://github.com/180254/test.git")
                            .setCredentialsProvider(credentials)
                            .call();
                }

            }


        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }

    }
}
