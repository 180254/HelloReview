package pl.p.lodz.iis.hr.models.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = InputText.class, name = "text"),
        @JsonSubTypes.Type(value = InputScale.class, name = "scale")
})
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class Input implements Serializable {

    private static final long serialVersionUID = -8117462196984682568L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(FormViews.RESTPreview.class)
    private long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @JsonView
    private Question question;

    @Column(nullable = false)
    @JsonView({FormViews.RESTPreview.class, FormViews.XMLTemplate.class})
    private boolean required = true;

    @Column(nullable = false, length = 255)
    @JsonView({FormViews.RESTPreview.class, FormViews.XMLTemplate.class})
    @NotBlank @Length(min = 1, max = 255)
    private String label;

    Input() {
    }

    protected Input(Question question, String label) {
        this.question = question;
        this.label = label;
    }

    public long getId() {
        return id;
    }

    public Question getQuestion() {
        return question;
    }

    void setQuestion(Question question) {
        this.question = question;
    }

    public boolean isRequired() {
        return required;
    }

    void setRequired(boolean required) {
        this.required = required;
    }

    public String getLabel() {
        return label;
    }

    void setLabel(String label) {
        this.label = label;
    }
}
