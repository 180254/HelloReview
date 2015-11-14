package pl.p.lodz.iis.hr.utils;

import pl.p.lodz.iis.hr.exceptions.GitHubCommunicationException;

import java.io.IOException;

@SuppressWarnings({"ErrorNotRethrown", "ProhibitedExceptionThrown"})
public final class GitHubExecutor {

    private GitHubExecutor() {
    }

    public static <T> T ex(GitHubExecutorFI<T> executor) {

        try {
            return executor.execute();

        } catch (IOException e) {
            throw new GitHubCommunicationException(e.getMessage(), e);

            // oh, it may be wrapped wih Error ;/
        } catch (Error e) {
            if (e.getCause() instanceof IOException) {
                throw new GitHubCommunicationException(e.getMessage(), e);
            } else {
                throw new Error(e);
            }
        }
    }

    public static void ex(GitHubExecutorFI2 executor) {
        GitHubExecutor.<Void>ex(() -> {
            executor.execute();
            return null;
        });
    }
}
