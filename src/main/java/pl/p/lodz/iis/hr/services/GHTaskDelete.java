package pl.p.lodz.iis.hr.services;

import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.p.lodz.iis.hr.utils.ExceptionUtil;
import pl.p.lodz.iis.hr.utils.GHExecutor;
import pl.p.lodz.iis.hr.utils.GHIRunner2;

class GHTaskDelete implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GHTaskDelete.class);

    private final String repoName;

    GHTaskDelete(String repoName) {
        this.repoName = repoName;
        LOGGER.info("{} Repo delete scheduled.", repoName);
    }

    @Override
    public void run() {
        LOGGER.info("{} Repo deleting.", repoName);
        GitHub ghWait = ApplicationContextProvider.getBean("ghWait", GitHub.class);

        GHIRunner2 deleteRunner = () -> ghWait.getMyself().getRepository(repoName).delete();
        boolean success = !ExceptionUtil.isExceptionThrown2(() -> GHExecutor.ex(deleteRunner));

        LOGGER.info("{} Repo delete status succeeded = {}", repoName, success);
    }
}
