package pl.p.lodz.iis.hr.utils;

import pl.p.lodz.iis.hr.exceptions.GHCommunicationException;

import java.io.IOException;

/**
 * Additional wrapper for getting information from GitHub api using org.kohsuke.github.GitHub.<br/>
 * Unfortunately, it not only throws IOException, as documented.<br/>
 * Experimentally found out that IOEception may be wrapper in Error.<br/>
 * This class mainly to unwrap it.<br/>
 * Additionally, exceptions are converted to internal GHCommunicationException exception.
 */
public final class GHExecutor {

    private GHExecutor() {
    }

    public static <T> T ex(GHIRunner1<T> runner) throws GHCommunicationException {

        try {
            return runner.execute();

        } catch (IOException e) {
            throw new GHCommunicationException(e.getMessage(), e);

            // oh, it may be wrapped wih Error ;/
        } catch (Error e) {
            if (e.getCause() instanceof IOException) {
                throw new GHCommunicationException(e.getMessage(), e);
            } else {
                throw new Error(e);
            }
        }
    }

    public static void ex(GHIRunner2 runner) throws GHCommunicationException {
        GHExecutor.<Void>ex(() -> {
            runner.execute();
            return null;
        });
    }
}
