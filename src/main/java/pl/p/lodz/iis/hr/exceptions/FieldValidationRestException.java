package pl.p.lodz.iis.hr.exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FieldValidationRestException extends Exception {

    private static final long serialVersionUID = -3990386793412920814L;

    private final List<String> fieldErrors;

    public FieldValidationRestException(String error) {
        fieldErrors = Collections.singletonList(error);
    }

    public FieldValidationRestException(List<String> fieldErrors) {
        this.fieldErrors = new ArrayList<>(fieldErrors);
    }

    public List<String> getFieldErrors() {
        return Collections.unmodifiableList(fieldErrors);
    }
}
