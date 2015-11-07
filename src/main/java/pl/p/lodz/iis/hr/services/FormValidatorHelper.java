package pl.p.lodz.iis.hr.services;

import org.springframework.validation.*;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.models.forms.Input;
import pl.p.lodz.iis.hr.models.forms.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class FormValidatorHelper {

    private final Validator validator;
    private final Form form;
    private final List<String> errorsList = new ArrayList<>(10);

    FormValidatorHelper(Validator validator, Form form) {
        this.validator = validator;
        this.form = form;
    }

    public List<String> validate() {
        errorsList.clear();
        String errorPathF = "form";

        validate2(form, errorPathF);

        List<Question> questions = form.getQuestions();
        int questionSize = questions.size();
        for (int qCnt = 0; qCnt < questionSize; qCnt++) {
            Question question = questions.get(qCnt);
            String errorPathQ = String.format("%s > question(%d)", errorPathF, qCnt);

            validate2(question, errorPathQ);

            List<Input> inputs = question.getInputs();
            int inputSize = inputs.size();
            for (int iCnt = 0; iCnt < inputSize; iCnt++) {
                Input input = inputs.get(iCnt);
                String errorPathI = String.format("%s > input(%d)", errorPathQ, iCnt);

                validate2(input, errorPathI);
            }
        }

        return new ArrayList<>(errorsList);
    }

    private void validate2(Object object, String errorPath) {
        BindingResult bindingResult = new DataBinder(object).getBindingResult();
        validator.validate(object, bindingResult);
        transformErrorsToStrings(bindingResult, errorPath);
    }

    private void transformErrorsToStrings(Errors errors, String errorPath) {
        List<String> collect =
                errors.getAllErrors().stream()
                        .map(objectError ->
                                String.format("%s > %s: [%s]",
                                        errorPath,
                                        ((FieldError) objectError).getField(),
                                        objectError.getDefaultMessage()))
                        .collect(Collectors.toList());

        errorsList.addAll(collect);
    }
}
