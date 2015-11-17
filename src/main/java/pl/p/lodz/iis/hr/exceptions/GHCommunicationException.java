package pl.p.lodz.iis.hr.exceptions;

public class GHCommunicationException extends Exception {

    private static final long serialVersionUID = 5485100320644397773L;

    public GHCommunicationException() {
    }

    public GHCommunicationException(String message) {
        super(message);
    }

    public GHCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GHCommunicationException(Throwable cause) {
        super(cause);
    }
}
