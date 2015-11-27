package pl.p.lodz.iis.hr.services;

import org.jetbrains.annotations.Nullable;
import pl.p.lodz.iis.hr.models.response.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Util class for displaying review commission form.<br/>
 * Main task of this is returning answer for given input.
 */
public class AnswerProvider {

    private final Map<Long, String> answersMap = new HashMap<>(10);

    public AnswerProvider(@Nullable Response response) {
        if (response == null) {
            return;
        }

        response.getAnswers()
                .forEach(answer ->
                        answersMap.put(answer.getInput().getId(), answer.getNotNullAnswer())
                );
    }

    public String get(long inputID) {
        return answersMap.containsKey(inputID)
                ? answersMap.get(inputID)
                : "";
    }
}
