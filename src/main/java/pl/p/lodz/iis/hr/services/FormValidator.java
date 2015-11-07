package pl.p.lodz.iis.hr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;
import pl.p.lodz.iis.hr.models.forms.Form;

import java.util.List;

@Service
public class FormValidator {

    @Autowired private Validator validator;
    @Autowired private MessageSource messageSource;

    public List<String> validate(Form form) {
        return new FormValidatorHelper(validator, form).validate();
    }
}
