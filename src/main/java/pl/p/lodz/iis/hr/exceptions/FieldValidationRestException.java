package pl.p.lodz.iis.hr.exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FieldValidationRestException extends Exception {

    private static final long serialVersionUID = -3990386793412920814L;
    private final List<String> errors;

    public FieldValidationRestException(List<String> errors) {
        this.errors = new ArrayList<>(errors);
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
