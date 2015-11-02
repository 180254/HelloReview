package pl.p.lodz.iis.hr.models.forms.fromxml;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "form")
public class FormPOJO {

    @NotNull
    @NotBlank
    @Size(min = 1, max = 255)
    @JsonProperty("name")
    private String name;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 5120)
    @JacksonXmlCData
//    @JacksonXmlText
    @JacksonXmlProperty(localName = "description") private String description;

    @NotNull
    @JsonProperty("question")
    private List<QuestionPOJO> questions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<QuestionPOJO> getQuestions() {
        return new ArrayList<>(questions);
    }

    public void setQuestions(List<QuestionPOJO> questions) {
        this.questions = new ArrayList<>(questions);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
