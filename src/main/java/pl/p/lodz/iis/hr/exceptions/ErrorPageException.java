package pl.p.lodz.iis.hr.exceptions;

public class ErrorPageException extends Exception {

    private static final long serialVersionUID = 6416766300104578903L;

    public final int statusCode;

    public ErrorPageException(int statusCode) {
        super(String.valueOf(statusCode));

        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
