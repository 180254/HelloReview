package pl.p.lodz.iis.hr.models.forms.questions;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Input implements Serializable {

    private static final long serialVersionUID = -8117462196984682568L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "input_id")
    private long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Question question;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private boolean required = true;

    Input() {
    }

    public Input(Question question, String label) {
        this.question = question;
        this.label = label;
    }

    public long getId() {
        return id;
    }

    public Question getQuestion() {
        return question;
    }

    public String getLabel() {
        return label;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
