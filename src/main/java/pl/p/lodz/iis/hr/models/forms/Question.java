package pl.p.lodz.iis.hr.models.forms;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import pl.p.lodz.iis.hr.models.JSONViews;
import pl.p.lodz.iis.hr.models.RelationsAware;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Question implements Serializable, RelationsAware {

    private static final long serialVersionUID = -1242802972920860558L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView
    private long id;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @JsonView
    private Form form;

    @Column(nullable = false, length = 255)
    @JsonView(JSONViews.FormParseXML.class)
    private String questionText;

    @Column(nullable = true, length = 4095)
    @JsonView(JSONViews.FormParseXML.class)
    private String additionalTips;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "question", orphanRemoval = true)
    @JsonView(JSONViews.FormParseXML.class)
    @JsonProperty("input")
    private List<Input> inputs;

    Question() {
    }

    public Question(Form form, String questionText, String additionalTips) {
        this.questionText = questionText;
        this.additionalTips = additionalTips;
        this.form = form;
        inputs = new ArrayList<>(10);
    }

    public long getId() {
        return id;
    }

    public Form getForm() {
        return form;
    }

    /* package */ void setForm(Form form) {
        this.form = form;
    }

    @NotBlank
    @Length(min = 1, max = 255)
    public String getQuestionText() {
        return questionText;
    }

    /* package */ void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    @Length(min = 0, max = 4095)
    public String getAdditionalTips() {
        return additionalTips;
    }

    /* package */ void setAdditionalTips(String additionalTips) {
        this.additionalTips = additionalTips;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    /* package */ void setInputs(List<Input> inputs) {
        this.inputs = inputs;
    }

    @Override
    public void fixRelations() {
        for (Input input : inputs) {
            input.setQuestion(this);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
