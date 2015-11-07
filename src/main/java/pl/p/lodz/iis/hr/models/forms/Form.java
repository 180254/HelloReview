package pl.p.lodz.iis.hr.models.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import pl.p.lodz.iis.hr.models.RelationsAware;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@JacksonXmlRootElement(localName = "form")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Form implements Serializable, RelationsAware {

    private static final long serialVersionUID = -6520652024047473630L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(FormViews.RESTPreview.class)
    private long id;

    @Column(nullable = false, length = 255)
    @JsonView(FormViews.RESTPreview.class)
    @NotBlank
    @Length(min = 1, max = 255)
    private String name;

    @Column(nullable = false, length = 4095)
    @JsonView({FormViews.RESTPreview.class, FormViews.ParseXML.class})
    @NotBlank
    @Length(min = 1, max = 4095)
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "form", orphanRemoval = true)
    @JsonView({FormViews.RESTPreview.class, FormViews.ParseXML.class})
    @JsonProperty("question")
    private final List<Question> questions = new ArrayList<>(10);

    @Column(nullable = false)
    @JsonView(FormViews.RESTPreview.class)
    private boolean temporary;

    Form() {
    }

    public Form(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public List<Question> getQuestions() {
        return new ArrayList<>(questions);
    }

    public boolean isTemporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

    @Override
    public void fixRelations() {
        for (Question question : questions) {
            question.setForm(this);
            question.fixRelations();
        }
    }
}
