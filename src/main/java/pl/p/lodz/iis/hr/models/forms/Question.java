package pl.p.lodz.iis.hr.models.forms;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
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
    @JsonView(FormViews.RESTPreview.class)
    private long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @JsonView
    private Form form;

    @Column(nullable = false, length = 255)
    @JsonView({FormViews.RESTPreview.class, FormViews.XMLTemplate.class})
    @NotBlank @Length(min = 1, max = 255)
    private String questionText;

    @Column(nullable = true, length = 4095)
    @JsonView({FormViews.RESTPreview.class, FormViews.XMLTemplate.class})
    @Length(min = 0, max = 4095)
    private String additionalTips;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "question", orphanRemoval = true)
    @JsonView({FormViews.RESTPreview.class, FormViews.XMLTemplate.class})
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @JsonProperty("input")
    private final List<Input> inputs = new ArrayList<>(10);

    Question() {
    }

    public Question(Form form, String questionText, String additionalTips) {
        this.questionText = questionText;
        this.additionalTips = additionalTips;
        this.form = form;
    }

    public long getId() {
        return id;
    }

    public Form getForm() {
        return form;
    }

    void setForm(Form form) {
        this.form = form;
    }

    public String getQuestionText() {
        return questionText;
    }

    void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getAdditionalTips() {
        return additionalTips;
    }

    void setAdditionalTips(String additionalTips) {
        this.additionalTips = additionalTips;
    }

    public List<Input> getInputs() {
        return new ArrayList<>(inputs);
    }

    @Override
    public void fixRelations() {
        for (Input input : inputs) {
            input.setQuestion(this);
        }
    }
}