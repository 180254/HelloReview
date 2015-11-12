package pl.p.lodz.iis.hr.models.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import pl.p.lodz.iis.hr.models.JSONViews;
import pl.p.lodz.iis.hr.models.RelationsAware;
import pl.p.lodz.iis.hr.repositories.FormRepository;
import pl.p.lodz.iis.hr.services.UniqueName;

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
    @JsonView(JSONViews.FormRESTPreview.class)
    private String name;

    @Column(nullable = false, length = 4095)
    @JsonView({JSONViews.FormRESTPreview.class, JSONViews.FormParseXML.class})
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "form", orphanRemoval = true)
    @JsonView({JSONViews.FormRESTPreview.class, JSONViews.FormParseXML.class})
    @JsonProperty("question")
    private List<Question> questions;

    @Column(nullable = false)
    @JsonView(JSONViews.FormRESTPreview.class)
    private boolean temporary;

    @Column(nullable = false)
    @JsonView(JSONViews.FormRESTPreview.class)
    private LocalDateTime created;

    @Column(nullable = false)
    @JsonView(JSONViews.FormRESTPreview.class)
    private LocalDateTime updated;

    Form() {
    }

    public Form(String name, String description) {
        this.name = name;
        this.description = description;
        questions = new ArrayList<>(10);
    }

    public long getId() {
        return id;
    }

    @NotBlank
    @Length(min = 1, max = 255)
    @UniqueName(service = FormRepository.class)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotBlank
    @Length(min = 1, max = 4095)
    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    void setQuestions(List<Question> questions) {
        this.questions = questions;
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

    void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
        updated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = LocalDateTime.now();
    }

    @Override
    public void fixRelations() {
        for (Question question : questions) {
            question.setForm(this);
            question.fixRelations();
        }
    }
}
