package pl.p.lodz.iis.hr.models.forms.questions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pl.p.lodz.iis.hr.models.forms.Form;

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
    private long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Form form;

    @Column(nullable = false)
    private String questionText;

    @Column(nullable = true, length = 5120)
    private String additionalTips;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "question")
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
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
