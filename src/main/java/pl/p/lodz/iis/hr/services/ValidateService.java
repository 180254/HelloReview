package pl.p.lodz.iis.hr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ValidateService {

    @Autowired private Validator validator;
    @Autowired private LocaleService localeService;

    public List<String> validateField(Object object, String fieldName, String prefix) {

        BindingResult bindingResult = new DataBinder(object).getBindingResult();
        validator.validate(object, bindingResult);

        List<FieldError> fieldErrors = bindingResult.getFieldErrors(fieldName);
        return fieldErrors.stream().map(
                fieldError -> String.format("%s %s", prefix, localeService.getMessage(fieldError)))
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> validateFields(Object object, String[] fieldNames, String[] prefixes) {
        List<String> errors = new ArrayList<>(10);

        for (int i = 0, len = fieldNames.length; i < len; i++) {
            errors.addAll(validateField(object, fieldNames[i], prefixes[i]));
        }

        return errors;
    }
}
