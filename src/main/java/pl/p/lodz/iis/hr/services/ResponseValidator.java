package pl.p.lodz.iis.hr.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.exceptions.ErrorPageException;
import pl.p.lodz.iis.hr.exceptions.LocalizableErrorRestException;
import pl.p.lodz.iis.hr.models.forms.Input;
import pl.p.lodz.iis.hr.models.forms.InputScale;
import pl.p.lodz.iis.hr.models.forms.InputText;
import pl.p.lodz.iis.hr.models.response.Answer;
import pl.p.lodz.iis.hr.models.response.Response;
import pl.p.lodz.iis.hr.utils.ExceptionUtil;
import pl.p.lodz.iis.hr.utils.ProxyUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Customized validator for model "Response".<br/>
 * It is not field validating, it checks if response is proper:<br/>
 * - answers for all inputs are provided<br/>
 * - ans answer has proper type (ex. is number if it should be).
 */
@Service
public class ResponseValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseValidator.class);

    public void validate(Response response)
            throws ErrorPageException, LocalizableErrorRestException {

        LOGGER.debug("validation of {}", response);

        ensureAnswersHarmony(response); // ErrorPageException.BAD_REQUEST
        ensureInputScaleAnswersAreNumber(response); // ErrorPageException.BAD_REQUEST
        ensureInputScaleAnswersAreInRange(response); // ErrorPageException.BAD_REQUEST
        ensureInputTextAnswersLenAreInRange(response); // ErrorPageException.BAD_REQUEST
        ensureAllRequiredAnswersGiven(response); // LocalizableErrorRestException.MISSING_ANSWER

        LOGGER.debug("validation successfully ended.");
    }

    private void ensureAnswersHarmony(Response response) throws ErrorPageException {
        LOGGER.debug("validation ensureAnswersHarmony started.");

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
            LOGGER.debug("validation ensureAnswersHarmony failed on first condition.");
            LOGGER.debug("diff1: {}", diff1);

            throw new ErrorPageException(HttpServletResponse.SC_BAD_REQUEST);
        }

        // verify all answer provided
        if (!diff2.isEmpty()) {
            LOGGER.debug("validation ensureAnswersHarmony failed on second condition.");
            LOGGER.debug("diff2: {}", diff2);

            throw new ErrorPageException(HttpServletResponse.SC_BAD_REQUEST);
        }

        LOGGER.debug("validation ensureAnswersHarmony successfully ended.");

    }

    private void ensureInputScaleAnswersAreNumber(Response response) throws ErrorPageException {
        LOGGER.debug("validation ensureInputScaleAnswersAreNumber started.");

        boolean properScaleAnswers = response.getAnswers().stream()
                .filter(answer1 -> ProxyUtils.isInstanceOf(answer1.getInput(), InputScale.class))
                .allMatch(answer1 -> !ExceptionUtil.isExceptionThrown1(answer1::getAnswerAsNumber));

        if (!properScaleAnswers) {
            LOGGER.debug("validation ensureAnswersHarmony failed.");
            throw new ErrorPageException(HttpServletResponse.SC_BAD_REQUEST);
        }

        LOGGER.debug("validation ensureInputScaleAnswersAreNumber successfully ended.");
    }

    private void ensureInputScaleAnswersAreInRange(Response response) throws ErrorPageException {
        LOGGER.debug("validation ensureInputScaleAnswersAreInRange started.");

        boolean allAreInRange = response.getAnswers().stream()
                .filter(answer1 -> ProxyUtils.isInstanceOf(answer1.getInput(), InputScale.class))
                .filter(answer1 -> answer1.getAnswer() != null)

                .allMatch(answer1 -> isBetweenInclusiveLong(
                        Long.parseLong(answer1.getAnswer()),
                        ((InputScale) answer1.getInput()).getFromS(),
                        ((InputScale) answer1.getInput()).getToS()
                ));

        if (!allAreInRange) {
            LOGGER.debug("validation ensureInputScaleAnswersAreInRange failed.");
            throw new ErrorPageException(HttpServletResponse.SC_BAD_REQUEST);
        }

        LOGGER.debug("validation ensureInputScaleAnswersAreInRange successfully ended.");
    }

    private void ensureInputTextAnswersLenAreInRange(Response response) throws ErrorPageException {
        LOGGER.debug("validation ensureInputTextAnswersLenAreInRange started.");

        boolean allAreInRange = response.getAnswers().stream()
                .filter(answer1 -> ProxyUtils.isInstanceOf(answer1.getInput(), InputText.class))
                .filter(answer1 -> answer1.getAnswer() != null)
                .allMatch(answer -> isBetweenInclusiveInt(answer.getAnswer().length(), 1, Answer.MAX_LENGTH));

        if (!allAreInRange) {
            LOGGER.debug("validation ensureInputTextAnswersLenAreInRange failed.");
            throw new ErrorPageException(HttpServletResponse.SC_BAD_REQUEST);
        }

        LOGGER.debug("validation ensureInputTextAnswersLenAreInRange successfully ended.");
    }

    private void ensureAllRequiredAnswersGiven(Response response) throws LocalizableErrorRestException {
        LOGGER.debug("validation ensureAllRequiredAnswersGiven started.");

        boolean allAnswersGiven = response.getAnswers().stream()
                .filter(answer1 -> answer1.getInput().isRequired())
                .allMatch(answer1 -> answer1.getAnswer() != null);

        if (!allAnswersGiven) {
            LOGGER.debug("validation ensureAllRequiredAnswersGiven failed.");
            throw new LocalizableErrorRestException("p.response.validate.missing.answer");
        }

        LOGGER.debug("validation ensureAllRequiredAnswersGiven successfully ended.");
    }

    private boolean isBetweenInclusiveInt(int x, int min, int max) {
        return (x >= min) && (x <= max);
    }

    private boolean isBetweenInclusiveLong(long x, long min, long max) {
        return (x >= min) && (x <= max);
    }
}
