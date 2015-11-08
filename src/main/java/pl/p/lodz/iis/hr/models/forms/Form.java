package pl.p.lodz.iis.hr.models.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import pl.p.lodz.iis.hr.models.JSONViews;
import pl.p.lodz.iis.hr.models.RelationsAware;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@JacksonXmlRootElement(localName = "form")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Form implements Serializable, RelationsAware {

    private static final long serialVersionUID = -6520652024047473630L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(JSONViews.FormRESTPreview.class)
    private long id;

    @Column(nullable = false, length = 255, unique = true)
    @NotBlank
    @Length(min = 1, max = 255)
    @JsonView(JSONViews.FormRESTPreview.class)
    private String name;

    @Column(nullable = false, length = 4095)
    @NotBlank
    @Length(min = 1, max = 4095)
    @JsonView({JSONViews.FormRESTPreview.class, JSONViews.FormParseXML.class})
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "form", orphanRemoval = true)
    @JsonView({JSONViews.FormRESTPreview.class, JSONViews.FormParseXML.class})
    @JsonProperty("question")
    private final List<Question> questions = new ArrayList<>(10);

    @Column(nullable = false)
    @JsonView(JSONViews.FormRESTPreview.class)
    private boolean temporary;

    @Column(nullable = false)
    @JsonView(JSONViews.FormRESTPreview.class)
    private final LocalDateTime created = LocalDateTime.now();

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

    public LocalDateTime getCreated() {
        return created;
    }

    @Override
    public void fixRelations() {
        for (Question question : questions) {
            question.setForm(this);
            question.fixRelations();
        }
    }
}
