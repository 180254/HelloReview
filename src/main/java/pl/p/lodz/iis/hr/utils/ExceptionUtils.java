package pl.p.lodz.iis.hr.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExceptionUtils {

    private ExceptionUtils() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionUtils.class);

    public static <T> boolean isExceptionThrown1(IRunner1<T> runner) {
        try {
            runner.run();
            return false;
        } catch (Exception e) {
            LOGGER.info("isExceptionThrown caught exception.", e);
            return true;
        }
    }

    public static boolean isExceptionThrown2(IRunner2 runner) {
        return isExceptionThrown1(() -> {
            runner.run();
            return null;
        });
    }

    public static <T> T ignoreException1(IRunner1<T> runner) {
        try {
            return runner.run();
        } catch (Exception e) {
            LOGGER.info("ignoreException caught exception.", e);
            return null;
        }
    }

    public static void ignoreException2(IRunner2 runner) {
        ignoreException1(() -> {
            runner.run();
            return null;
        });
    }

}
