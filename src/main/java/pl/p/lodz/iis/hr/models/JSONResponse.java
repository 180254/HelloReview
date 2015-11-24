package pl.p.lodz.iis.hr.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JSONResponse {

    @JsonProperty("commID")
    private final String commUUID;

    @JsonProperty("answers")
    private final List<JSONAnswer> jsonAnswers;

    @JsonCreator
    public JSONResponse(@JsonProperty("id") String commUUID,
                        @JsonProperty("answers") List<JSONAnswer> jsonAnswers) {

        this.commUUID = commUUID;
        this.jsonAnswers = new ArrayList<>(jsonAnswers);
    }

    public String getCommUUID() {
        return commUUID;
    }

    public List<JSONAnswer> getJsonAnswers() {
        return Collections.unmodifiableList(jsonAnswers);
    }
}
