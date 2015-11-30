package pl.p.lodz.iis.hr.services;

import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.p.lodz.iis.hr.utils.ExceptionUtils;
import pl.p.lodz.iis.hr.utils.GHExecutor;
import pl.p.lodz.iis.hr.utils.GHIRunner2;

/**
 * Repository deleter. Anonymous clone are not needed anymore, if review was deleted or closed.<br/>
 * We can clean "dummy account" and remove these clones.<br/>
 * Whole logic of deleting repository clone is covered by this class.
 */
class GHTaskDelete implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GHTaskDelete.class);

    private final String repoSimpleName;

    GHTaskDelete(String repoSimpleName) {
        this.repoSimpleName = repoSimpleName;
        LOGGER.info("{} Repo delete scheduled.", repoSimpleName);
    }

    @Override
    public void run() {
        LOGGER.info("{} Repo deleting.", repoSimpleName);
        GitHub ghWait = ApplicationContextProvider.getBean("ghWait", GitHub.class);

        GHIRunner2 deleteRunner = () -> ghWait.getMyself().getRepository(repoSimpleName).delete();
        boolean success = !ExceptionUtils.isExceptionThrown2(() -> GHExecutor.ex(deleteRunner));

        LOGGER.info("{} Repo delete status succeeded = {}", repoSimpleName, success);
    }
}
