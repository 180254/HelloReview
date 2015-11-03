package pl.p.lodz.iis.hr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.*;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.models.forms.questions.Input;
import pl.p.lodz.iis.hr.models.forms.questions.Question;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormValidator {

    @Autowired private Validator validator;
    @Autowired private MessageSource messageSource;

    public List<String> validate(Form form) {
        List<String> errors = new ArrayList<>(10);

        String errorPathF = "form";
        errors.addAll(validate2(form, errorPathF));

        List<Question> questions = form.getQuestions();
        int questionSize = questions.size();
        for (int qCnt = 0; qCnt < questionSize; qCnt++) {
            Question question = questions.get(qCnt);
            String errorPathQ = String.format("%s > question(%d)", errorPathF, qCnt);
            errors.addAll(validate2(question, errorPathQ));

            List<Input> inputs = question.getInputs();
            int inputSize = inputs.size();
            for (int iCnt = 0; iCnt < inputSize; iCnt++) {
                Input input = inputs.get(iCnt);
                String errorPathI = String.format("%s > input(%d)", errorPathQ, iCnt);
                errors.addAll(validate2(input, errorPathI));
            }
        }


        return errors;
    }

    private Collection<String> validate2(Object object, String errorPath) {
        Collection<String> errors = new ArrayList<>(10);

        BindingResult bindingResult = new DataBinder(object).getBindingResult();
        validator.validate(object, bindingResult);
        transformErrors(errors, bindingResult, errorPath);

        return errors;
    }

    private void transformErrors(Collection<String> errorsMessages, Errors errors, String errorPath) {
        errorsMessages.addAll(
                errors.getAllErrors().stream()
                        .map(objectError ->
                                String.format("%s > [%s: %s]",
                                        errorPath,
                                        ((FieldError) objectError).getField(),
                                        objectError.getDefaultMessage()))
                        .collect(Collectors.toList()));

    }
}
