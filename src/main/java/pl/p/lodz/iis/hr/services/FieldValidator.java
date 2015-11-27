package pl.p.lodz.iis.hr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import pl.p.lodz.iis.hr.exceptions.LocalizedErrorRestException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Util class for validate if field in model was properly filled.
 */
@Service
public class FieldValidator {

    private final Validator validator;
    private final LocaleService localeService;

    @Autowired
    public FieldValidator(Validator validator, LocaleService localeService) {
        this.validator = validator;
        this.localeService = localeService;
    }

    /**
     * Validator for one field.<br/>
     *
     * @param object    validated object
     * @param fieldName field name
     * @param prefix    field name localized name (ex: "course name" for object "course", and fieldName "name")
     * @return list of localized errors
     */
    public List<String> validateField(Object object, String fieldName, String prefix) {

        BindingResult bindingResult = new DataBinder(object).getBindingResult();
        validator.validate(object, bindingResult);
        List<FieldError> fieldErrors = bindingResult.getFieldErrors(fieldName);

        return fieldErrors.stream()
                .map(fieldError -> String.format("%s %s", prefix, localeService.get(fieldError)))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Validator for one field.<br/>
     * Uses same validation logic as 'validateField', but throws exception instead of returning list of errors.
     *
     * @param object    validated object
     * @param fieldName field name
     * @param prefix    field name localized name (ex: "course name" for object "course", and fieldName "name")
     * @throws LocalizedErrorRestException if any field issue found
     */
    public void validateFieldRestEx(Object object, String fieldName, String prefix)
            throws LocalizedErrorRestException {

        List<String> errors = validateField(object, fieldName, prefix);

        if (!errors.isEmpty()) {
            throw new LocalizedErrorRestException(errors);
        }
    }

    /**
     * Validator for multiple fields.
     *
     * @param object     validated object
     * @param fieldNames array of field names
     * @param prefixes   array of field localized name (ex: "course name" for object "course", and fieldName "name")
     * @return list of localized errors
     */
    public List<String> validateFields(Object object, String[] fieldNames, String[] prefixes) {
        List<String> errors = new ArrayList<>(fieldNames.length);

        for (int i = 0, len = fieldNames.length; i < len; i++) {
            errors.addAll(validateField(object, fieldNames[i], prefixes[i]));
        }

        return errors;
    }

    /**
     * Validator for multiple fields.
     * Uses same validation logic as 'validateFields', but throws exception instead of returning list of errors.
     *
     * @param object     validated object
     * @param fieldNames array of field names
     * @param prefixes   array of field localized name (ex: "course name" for object "course", and fieldName "name")
     * @throws LocalizedErrorRestException if any field issue found
     */
    public void validateFieldsRestEx(Object object, String[] fieldNames, String[] prefixes)
            throws LocalizedErrorRestException {

        List<String> errors = validateFields(object, fieldNames, prefixes);

        if (!errors.isEmpty()) {
            throw new LocalizedErrorRestException(errors);
        }
    }
}
