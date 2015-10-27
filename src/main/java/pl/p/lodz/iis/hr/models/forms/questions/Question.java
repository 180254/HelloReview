package pl.p.lodz.iis.hr.models.forms.questions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pl.p.lodz.iis.hr.models.forms.Form;

import javax.persistence.*;

@Entity
public class Question {

    @Id
    private long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Form form;

    @Column(nullable = false)
    private String questionText;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String question) {
        this.questionText = question;
    }
}
