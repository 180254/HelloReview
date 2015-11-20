package pl.p.lodz.iis.hr.exceptions;

public final class ResourceNotFoundRestException extends Exception {

    private static final long serialVersionUID = -8283053731171603752L;

    public ResourceNotFoundRestException() {
    }

    public ResourceNotFoundRestException(String message) {
        super(message);
    }

    public ResourceNotFoundRestException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundRestException(Throwable cause) {
        super(cause);
    }
}
