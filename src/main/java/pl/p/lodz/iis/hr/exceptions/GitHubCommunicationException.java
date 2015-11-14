package pl.p.lodz.iis.hr.exceptions;

public class GitHubCommunicationException extends Exception {

    private static final long serialVersionUID = 5485100320644397773L;

    public GitHubCommunicationException() {
    }

    public GitHubCommunicationException(String message) {
        super(message);
    }

    public GitHubCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitHubCommunicationException(Throwable cause) {
        super(cause);
    }
}
