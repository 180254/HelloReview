package pl.p.lodz.iis.hr.utils;

import pl.p.lodz.iis.hr.exceptions.GitHubCommunicationException;

import java.io.FileNotFoundException;
import java.io.IOException;

@SuppressWarnings({"ErrorNotRethrown", "ProhibitedExceptionThrown"})
public final class GitHubExecutor {

    private GitHubExecutor() {
    }

    public static void execute(GitHubExecutorFI executor) {

        try {
            executor.execute();

        } catch (FileNotFoundException e) {
            throw new GitHubCommunicationException("probably bad credentials", e);

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
}
