package pl.p.lodz.iis.hr.models.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.MoreObjects;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import pl.p.lodz.iis.hr.models.JSONViews;
import pl.p.lodz.iis.hr.models.RelationsAware;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.repositories.FormRepository;
import pl.p.lodz.iis.hr.services.UniqueName;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@JacksonXmlRootElement(localName = "form")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Form implements Serializable, RelationsAware {

    private static final long serialVersionUID = -6520652024047473630L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView
    private long id;

    @Column(nullable = false, length = 255, unique = true)
    @JsonView
    private String name;

    @Column(nullable = false, length = 4095)
    @JsonView(JSONViews.FormParseXML.class)
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "form", orphanRemoval = true)
        @JsonView(JSONViews.FormParseXML.class)
    @JsonProperty("question")
    private List<Question> questions;

    @OneToMany(cascade = {}, fetch = FetchType.LAZY, mappedBy = "form", orphanRemoval = true)
        @JsonView
    @JsonProperty("review")
    private List<Review> reviews;

    @Column(nullable = false)
    @JsonView
    private boolean temporary;

    @Column(nullable = false)
    @JsonView
    private LocalDateTime created;

    @Column(nullable = false)
    @JsonView
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

    /* package */ void setDescription(String description) {
        this.description = description;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    /* package */ void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    /* package */ void setReviews(List<Review> reviews) {
        this.reviews = reviews;
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

    /* package */ void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    /* package */ void setUpdated(LocalDateTime updated) {
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
        if (questions == null) {
            questions = new ArrayList<>(0);
        }

        for (Question question : questions) {
            question.setForm(this);
            question.fixRelations();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if ((obj == null) || !(obj instanceof Form)) return false;
        Form that = (Form) obj;
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
                .add("name", name)
                .add("description", description)
                .add("questions", questions)
                .add("reviews", reviews)
                .add("temporary", temporary)
                .add("created", created)
                .add("updated", updated)
                .toString();
    }
}
