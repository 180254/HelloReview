package pl.p.lodz.iis.hr.models.forms;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import pl.p.lodz.iis.hr.models.JSONViews;
import pl.p.lodz.iis.hr.models.RelationsAware;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Question implements Serializable, RelationsAware {

    private static final long serialVersionUID = -1242802972920860558L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView
    private long id;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(nullable = false)
    @Fetch(FetchMode.JOIN)
    @JsonView
    private Form form;

    @Column(nullable = false, length = 255)
    @JsonView(JSONViews.FormParseXML.class)
    private String questionText;

    @Column(nullable = true, length = 4095)
    @JsonView(JSONViews.FormParseXML.class)
    private String additionalTips;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "question", orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
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
        if (inputs == null) {
            inputs = new ArrayList<>(0);
        }

        for (Input input : inputs) {
            input.setQuestion(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if ((obj == null) || !(obj instanceof Question)) return false;
        Question that = (Question) obj;
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
                .add("form", form.getId())
                .add("questionText", questionText)
                .add("additionalTips", additionalTips)
                .add("inputs", inputs)
                .toString();
    }
}
