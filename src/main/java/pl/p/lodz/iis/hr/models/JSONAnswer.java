package pl.p.lodz.iis.hr.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JSONAnswer {

    @JsonProperty("inputID")
    private final long inputID;

    @JsonProperty("answer")
    private final String answer;

    @JsonCreator
    public JSONAnswer(@JsonProperty("inputID") long inputID,
                      @JsonProperty("answer") String answer) {

        this.inputID = inputID;
        this.answer = answer;
    }

    public long getInputID() {
        return inputID;
    }

    public String getAnswer() {
        return answer;
    }
}
