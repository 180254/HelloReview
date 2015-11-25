package pl.p.lodz.iis.hr.services;

import org.jetbrains.annotations.Nullable;
import pl.p.lodz.iis.hr.models.response.Answer;
import pl.p.lodz.iis.hr.models.response.Response;

import java.util.HashMap;
import java.util.Map;

public class AnswerProvider {

    private final Map<Long, String> answersMap = new HashMap<>(10);

    public AnswerProvider(@Nullable Response response) {
        if (response == null) {
            return;
        }

        for (Answer answer : response.getAnswers()) {
            answersMap.put(answer.getInput().getId(), answer.getNotNullAnswer());
        }
    }

    public String get(long inputID) {
        return answersMap.containsKey(inputID)
                ? answersMap.get(inputID)
                : "";
    }
}
