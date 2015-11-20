package pl.p.lodz.iis.hr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import pl.p.lodz.iis.hr.exceptions.FieldValidationRestException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FieldValidator {

    private final Validator validator;
    private final LocaleService localeService;

    @Autowired
    public FieldValidator(Validator validator, LocaleService localeService) {
        this.validator = validator;
        this.localeService = localeService;
    }

    public List<String> validateField(Object object, String fieldName, String prefix) {

        BindingResult bindingResult = new DataBinder(object).getBindingResult();
        validator.validate(object, bindingResult);
        List<FieldError> fieldErrors = bindingResult.getFieldErrors(fieldName);

        return fieldErrors.stream()
                .map(fieldError -> String.format("%s %s", prefix, localeService.get(fieldError)))
                .sorted()
                .collect(Collectors.toList());
    }

    public void validateFieldRestEx(Object object, String fieldName, String prefix)
            throws FieldValidationRestException {
        List<String> errors = validateField(object, fieldName, prefix);

        if (!errors.isEmpty()) {
            throw new FieldValidationRestException(errors);
        }
    }

    public List<String> validateFields(Object object, String[] fieldNames, String[] prefixes) {
        List<String> errors = new ArrayList<>(fieldNames.length);

        for (int i = 0, len = fieldNames.length; i < len; i++) {
            errors.addAll(validateField(object, fieldNames[i], prefixes[i]));
        }

        return errors;
    }

    public void validateFieldsdRestEx(Object object, String[] fieldNames, String[] prefixes)
            throws FieldValidationRestException {
        List<String> errors = validateFields(object, fieldNames, prefixes);

        if (!errors.isEmpty()) {
            throw new FieldValidationRestException(errors);
        }
    }
}
