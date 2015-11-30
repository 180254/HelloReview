package pl.p.lodz.iis.hr.models.forms;

import com.fasterxml.jackson.annotation.*;
import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import pl.p.lodz.iis.hr.models.JSONViews;
import pl.p.lodz.iis.hr.models.response.Answer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = InputText.class, name = "text"),
        @JsonSubTypes.Type(value = InputScale.class, name = "scale")
})
public abstract class Input implements Serializable {

    private static final long serialVersionUID = -8117462196984682568L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @JsonView
    private long id;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @Fetch(FetchMode.JOIN)
    @JsonView
    private Question question;

    @Column(nullable = false)
    @JsonView(JSONViews.FormParseXML.class)
    private boolean required = true;

    @Column(nullable = false, length = 255)
    @JsonView(JSONViews.FormParseXML.class)
    private String label;

    @OneToMany(cascade = {}, fetch = FetchType.LAZY, mappedBy = "input", orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    @JsonView
    @JsonProperty("answer")
    private List<Answer> answers;

    Input() {
    }

    protected Input(Question question, String label) {
        this.question = question;
        this.label = label;
        this.answers = new ArrayList<>(3);
    }

    public long getId() {
        return id;
    }

    public Question getQuestion() {
        return question;
    }

    /* package */ void setQuestion(Question question) {
        this.question = question;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @NotBlank
    @Length(min = 1, max = 255)
    public String getLabel() {
        return label;
    }

    /* package */ void setLabel(String label) {
        this.label = label;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    /* package */ void setAnswers(List<Answer> answer) {
        this.answers = answer;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if ((obj == null) || !(obj instanceof Input)) return false;
        Input that = (Input) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("question", question.getId())
                .add("required", required)
                .add("label", label)
                .toString();
    }


}
