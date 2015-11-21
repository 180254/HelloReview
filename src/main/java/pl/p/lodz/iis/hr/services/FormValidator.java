package pl.p.lodz.iis.hr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;
import pl.p.lodz.iis.hr.exceptions.LocalizedErrorRestException;
import pl.p.lodz.iis.hr.models.forms.Form;

import java.util.List;

@Service
public class FormValidator {

    private final Validator validator;
    private final LocaleService localeService;

    @Autowired
    public FormValidator(Validator validator, LocaleService localeService) {
        this.validator = validator;
        this.localeService = localeService;
    }

    public List<String> validate(Form form) {
        return new FormValidator2(validator, localeService, form).validate();
    }

    public void validateRestEx(Form form) throws LocalizedErrorRestException {

        List<String> validate = validate(form);
        if (!validate.isEmpty()) {
            throw new LocalizedErrorRestException(validate);
        }
    }
}
