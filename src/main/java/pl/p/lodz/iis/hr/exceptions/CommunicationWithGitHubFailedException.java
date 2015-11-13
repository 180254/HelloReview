package pl.p.lodz.iis.hr.exceptions;

public class CommunicationWithGitHubFailedException extends RuntimeException {

    private static final long serialVersionUID = 5485100320644397773L;

    public CommunicationWithGitHubFailedException() {
    }

    public CommunicationWithGitHubFailedException(String message) {
        super(message);
    }

    public CommunicationWithGitHubFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommunicationWithGitHubFailedException(Throwable cause) {
        super(cause);
    }
}
