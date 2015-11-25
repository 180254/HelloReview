package pl.p.lodz.iis.hr.services;

import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.exceptions.ErrorPageException;
import pl.p.lodz.iis.hr.exceptions.LocalizableErrorRestException;
import pl.p.lodz.iis.hr.models.forms.Input;
import pl.p.lodz.iis.hr.models.forms.InputScale;
import pl.p.lodz.iis.hr.models.forms.InputText;
import pl.p.lodz.iis.hr.models.response.Answer;
import pl.p.lodz.iis.hr.models.response.Response;
import pl.p.lodz.iis.hr.utils.ExceptionUtil;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResponseValidator {

    public void validate(Response response)
            throws ErrorPageException, LocalizableErrorRestException {

        ensureAnswersHarmony(response); // ErrorPageException.BAD_REQUEST
        ensureInputScaleAnswersAreNumber(response); // ErrorPageException.BAD_REQUEST
        ensureInputScaleAnswersAreInRange(response); // ErrorPageException.BAD_REQUEST
        ensureInputTextAnswersLenAreInRange(response); // ErrorPageException.BAD_REQUEST
        ensureAllRequiredAnswersGiven(response); // LocalizableErrorRestException.MISSING_ANSWER
    }

    private void ensureAnswersHarmony(Response response) throws ErrorPageException {
        List<Input> reviewInputs = response.getCommission().getReview()
                .getForm().getQuestions().stream()
                .flatMap(q -> q.getInputs().stream())
                .collect(Collectors.toList());

        List<Input> answerInputs = response.getAnswers().stream()
                .map(Answer::getInput)
                .collect(Collectors.toList());

        Collection<Input> diff1 = new ArrayList<>(answerInputs);
        diff1.removeAll(reviewInputs);

        Collection<Input> diff2 = new ArrayList<>(reviewInputs);
        diff2.removeAll(answerInputs);

        // verify there are now extra answers, for other non-review-form-inputs
        if (!diff1.isEmpty()) {
            throw new ErrorPageException(HttpServletResponse.SC_BAD_REQUEST);
        }

        // verify all answer provided
        if (!diff2.isEmpty()) {
            throw new ErrorPageException(HttpServletResponse.SC_BAD_REQUEST);
        }
    }


    private void ensureInputScaleAnswersAreNumber(Response response) throws ErrorPageException {
        boolean properScaleAnswers = response.getAnswers().stream()
                .filter(answer1 -> answer1.getInput() instanceof InputScale)
                .allMatch(answer1 -> !ExceptionUtil.isExceptionThrown1(answer1::getAnswerAsNumber));

        if (!properScaleAnswers) {
            throw new ErrorPageException(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void ensureInputScaleAnswersAreInRange(Response response) throws ErrorPageException {
        boolean allAreInRange = response.getAnswers().stream()
                .filter(answer1 -> answer1.getInput() instanceof InputScale)
                .filter(answer1 -> answer1.getAnswer() != null)

                .allMatch(answer1 -> isBetweenInclusiveLong(
                        Long.parseLong(answer1.getAnswer()),
                        ((InputScale) answer1.getInput()).getFromS(),
                        ((InputScale) answer1.getInput()).getToS()
                ));

        if (!allAreInRange) {
            throw new ErrorPageException(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void ensureInputTextAnswersLenAreInRange(Response response) throws ErrorPageException {
        boolean allAreInRange = response.getAnswers().stream()
                .filter(answer1 -> answer1.getInput() instanceof InputText)
                .filter(answer1 -> answer1.getAnswer() != null)
                .allMatch(answer -> isBetweenInclusiveInt(answer.getAnswer().length(), 1, 1000));

        if (!allAreInRange) {
            throw new ErrorPageException(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void ensureAllRequiredAnswersGiven(Response response) throws LocalizableErrorRestException {
        boolean allAnswersGiven = response.getAnswers().stream()
                .filter(answer1 -> answer1.getInput().isRequired())
                .allMatch(answer1 -> answer1.getAnswer() != null);

        if (!allAnswersGiven) {
            throw new LocalizableErrorRestException("p.response.validate.missing.answer");
        }
    }

    private boolean isBetweenInclusiveInt(int x, int min, int max) {
        return (x >= min) && (x <= max);
    }

    private boolean isBetweenInclusiveLong(long x, long min, long max) {
        return (x >= min) && (x <= max);
    }
}
