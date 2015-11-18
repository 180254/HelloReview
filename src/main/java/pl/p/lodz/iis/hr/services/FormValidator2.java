package pl.p.lodz.iis.hr.services;

import org.springframework.validation.*;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.models.forms.Input;
import pl.p.lodz.iis.hr.models.forms.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class FormValidator2 {

    private final Validator validator;
    private final LocaleService localeService;
    private final Form form;
    private final List<String> errorsList = new ArrayList<>(10);

    FormValidator2(Validator validator, LocaleService localeService, Form form) {
        this.validator = validator;
        this.localeService = localeService;
        this.form = form;
    }

    public List<String> validate() {
        errorsList.clear();
        String errorPathF = "form";

        validate2(form, errorPathF);
        validateQuestions(errorPathF, form.getQuestions());

        return new ArrayList<>(errorsList);
    }

    private void validateQuestions(String errorPathF, List<Question> questions) {
        int questionSize = questions.size();
        for (int qCnt = 0; qCnt < questionSize; qCnt++) {
            Question question = questions.get(qCnt);
            String errorPathQ = String.format("%s > question(%d)", errorPathF, qCnt);

            validate2(question, errorPathQ);
            validateInputs(errorPathQ, question.getInputs());
        }
    }

    private void validateInputs(String errorPathQ, List<Input> inputs) {
        int inputSize = inputs.size();
        for (int iCnt = 0; iCnt < inputSize; iCnt++) {
            Input input = inputs.get(iCnt);
            String errorPathI = String.format("%s > input(%d)", errorPathQ, iCnt);

            validate2(input, errorPathI);
        }
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
                                        localeService.get(objectError))
                        )
                        .collect(Collectors.toList());

        errorsList.addAll(collect);
    }
}
