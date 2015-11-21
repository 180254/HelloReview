package pl.p.lodz.iis.hr.exceptions;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LocalizedErrorRestException extends Exception {

    private static final long serialVersionUID = -3990386793412920814L;

    private final int statusCode;
    private final List<String> errors;

    public LocalizedErrorRestException(int statusCode, String error) {
        this.statusCode = statusCode;
        this.errors = Collections.singletonList(error);
    }

    public LocalizedErrorRestException(int statusCode, List<String> errors) {
        this.statusCode = statusCode;
        this.errors = new ArrayList<>(errors);
    }

    public LocalizedErrorRestException(String error) {
        this.statusCode = HttpServletResponse.SC_BAD_REQUEST;
        this.errors = Collections.singletonList(error);
    }

    public LocalizedErrorRestException(List<String> errors) {
        this.statusCode = HttpServletResponse.SC_BAD_REQUEST;
        this.errors = new ArrayList<>(errors);
    }


    public int getStatusCode() {
        return statusCode;
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

}
