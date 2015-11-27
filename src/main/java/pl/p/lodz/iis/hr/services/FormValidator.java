package pl.p.lodz.iis.hr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;
import pl.p.lodz.iis.hr.exceptions.LocalizedErrorRestException;
import pl.p.lodz.iis.hr.models.forms.Form;

import java.util.List;

/**
 * Customized validator for model "Form".<br/>
 * While validating form is used special form of displaying list of errors, other than in "FieldValidator".
 */
@Service
public class FormValidator {

    private final Validator validator;
    private final LocaleService localeService;

    @Autowired
    public FormValidator(Validator validator, LocaleService localeService) {
        this.validator = validator;
        this.localeService = localeService;
    }

    /**
     * Validates form.
     *
     * @param form validated form
     * @return list of localized errors
     */
    public List<String> validate(Form form) {
        return new FormValidator2(validator, localeService, form).validate();
    }

    /**
     * Validates form.<br/>
     * Same logic as 'validate' method, but throws Exception instead of returning list of errors.
     *
     * @param form form validated form
     * @throws LocalizedErrorRestException if any any form issue was found.
     */
    public void validateRestEx(Form form) throws LocalizedErrorRestException {

        List<String> validate = validate(form);
        if (!validate.isEmpty()) {
            throw new LocalizedErrorRestException(validate);
        }
    }
}
