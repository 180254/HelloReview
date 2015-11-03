package pl.p.lodz.iis.hr.models.forms.questions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.models.forms.FormViews;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Question implements Serializable {

    private static final long serialVersionUID = -1242802972920860558L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    @JsonView(FormViews.RESTPreview.class)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonView()
    private Form form;

    @Column(nullable = false, length = 255)
    @JsonView({FormViews.RESTPreview.class, FormViews.XMLTemplate.class})
    @Length(min = 1, max = 255)
    @NotBlank
    private String questionText;

    @Column(nullable = true, length = 4095)
    @JsonView({FormViews.RESTPreview.class, FormViews.XMLTemplate.class})
    @Length(min = 1, max = 4095)
    private String additionalTips;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "question")
    @JsonView({FormViews.RESTPreview.class, FormViews.XMLTemplate.class})
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

    public String getQuestionText() {
        return questionText;
    }

    public String getAdditionalTips() {
        return additionalTips;
    }

    public List<Input> getInputs() {
        return new ArrayList<>(inputs);
    }
}
