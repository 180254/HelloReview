package pl.p.lodz.iis.hr.services;

import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.p.lodz.iis.hr.configuration.appconfig.AppConfig;
import pl.p.lodz.iis.hr.utils.ExceptionUtil;
import pl.p.lodz.iis.hr.utils.GitHubExecutor;
import pl.p.lodz.iis.hr.utils.IRunnerGH2;

public class GitDeleteTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitDeleteTask.class);

    private final GitHub gitHub;
    private final AppConfig appConfig;
    private final String repoName;

    public GitDeleteTask(GitHub gitHub, AppConfig appConfig, String repoName) {
        this.gitHub = gitHub;
        this.appConfig = appConfig;
        this.repoName = repoName;

        LOGGER.info("{} Repo delete scheduled.", repoName);
    }

    @Override
    public void run() {
        String dummyUsername = appConfig.getGitHubConfig().getDummy().getUsername();
        IRunnerGH2 deleteRunner = () -> gitHub.getRepository(String.format("%s/%s", dummyUsername, repoName)).delete();

        boolean success = ExceptionUtil.isExceptionThrown2(() -> GitHubExecutor.ex(deleteRunner));
        LOGGER.info("{} Repo delete status succeeded = {}", repoName, success);

    }
}
