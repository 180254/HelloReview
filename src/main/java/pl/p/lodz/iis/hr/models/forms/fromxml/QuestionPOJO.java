package pl.p.lodz.iis.hr.models.forms.fromxml;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotBlank;
import pl.p.lodz.iis.hr.models.forms.questions.Input;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class QuestionPOJO {

    @NotNull
    @NotBlank
    @Size(min = 1, max = 255)
    @JsonProperty("questionText")
    private String questionText;

    @Size(min = 0, max = 5120)
    @JsonProperty("extraText")
    @JacksonXmlCData
    private String extraText;

    @JsonProperty("input")
    private List<InputPOJO> inputs;

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getExtraText() {
        return extraText;
    }

    public void setExtraText(String extraText) {
        this.extraText = extraText;
    }

    public List<InputPOJO> getInputs() {
        return new ArrayList<>(inputs);
    }

    public void setInputs(List<InputPOJO> inputs) {
        this.inputs = new ArrayList<>(inputs);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
