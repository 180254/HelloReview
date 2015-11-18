package pl.p.lodz.iis.hr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;
import pl.p.lodz.iis.hr.models.forms.Form;

import java.util.List;

@Service
public class FormValidator {

    private Validator validator;
    private LocaleService localeService;

    @Autowired
    public FormValidator(Validator validator, LocaleService localeService) {
        this.validator = validator;
        this.localeService = localeService;
    }

    public List<String> validate(Form form) {
        return new FormValidator2(validator, localeService, form).validate();
    }
}
