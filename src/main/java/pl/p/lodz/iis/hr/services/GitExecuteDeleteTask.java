package pl.p.lodz.iis.hr.services;

import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.p.lodz.iis.hr.utils.ExceptionUtil;
import pl.p.lodz.iis.hr.utils.GitHubExecutor;
import pl.p.lodz.iis.hr.utils.IRunnerGH2;

class GitExecuteDeleteTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitExecuteDeleteTask.class);

    private final String repoName;

    GitExecuteDeleteTask(String repoName) {
        this.repoName = repoName;
        LOGGER.info("{} Repo delete scheduled.", repoName);
    }

    @Override
    public void run() {
        LOGGER.info("{} Deleting.", repoName);
        GitHub gitHubWait = ApplicationContextProvider.getBean("gitHubWait", GitHub.class);

        IRunnerGH2 deleteRunner = () -> gitHubWait.getMyself().getRepository(repoName).delete();
        boolean success = !ExceptionUtil.isExceptionThrown2(() -> GitHubExecutor.ex(deleteRunner));

        LOGGER.info("{} Repo delete status succeeded = {}", repoName, success);
    }
}
