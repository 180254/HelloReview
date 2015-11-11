package pl.p.lodz.iis.hr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.repositories.FormRepository;

import java.util.List;

@Service
public class FormValidator {

    @Autowired private Validator validator;
    @Autowired private FormRepository formRepository;
    @Autowired private LocaleService localeService;

    public List<String> validate(Form form) {
        return new FormValidatorHelper(validator, formRepository, localeService, form).validate();
    }
}
