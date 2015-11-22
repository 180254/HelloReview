package pl.p.lodz.iis.hr.models.courses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.MoreObjects;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Participant implements Serializable {

    private static final long serialVersionUID = -6918665306614378792L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView
    private long id;

    @Column(nullable = false, length = 255)
    @JsonView
    private String name;

    @Column(nullable = false, length = 255)
    @JsonView
    private String gitHubName;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @JsonView
    private Course course;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "assessor", orphanRemoval = true)
    @JsonView
    @JsonProperty("commissionAsAssessor")
    private List<Commission> commissionsAsAssessor;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "assessed", orphanRemoval = true)
    @JsonView
    @JsonProperty("commissionAsAssessed")
    private List<Commission> commissionsAsAssessed;

    @Column(nullable = false)
    @JsonView
    private LocalDateTime created;

    @Column(nullable = false)
    @JsonView
    private LocalDateTime updated;

    Participant() {
    }

    public Participant(Course course, String name, String gitHubName) {
        this.name = name;
        this.gitHubName = gitHubName;
        this.course = course;
    }

    public long getId() {
        return id;
    }

    @NotBlank
    @Length(min = 1, max = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotBlank
    @Length(min = 1, max = 255)
    public String getGitHubName() {
        return gitHubName;
    }

    public void setGitHubName(String gitHubName) {
        this.gitHubName = gitHubName;
    }

    public Course getCourse() {
        return course;
    }

    public List<Commission> getCommissionsAsAssessor() {
        return commissionsAsAssessor;
    }

    /* package */ void setCommissionsAsAssessor(List<Commission> commissionsAsAssessor) {
        this.commissionsAsAssessor = commissionsAsAssessor;
    }

    public List<Commission> getCommissionsAsAssessed() {
        return commissionsAsAssessed;
    }

    /* package */ void setCommissionsAsAssessed(List<Commission> commissionsAsAssessed) {
        this.commissionsAsAssessed = commissionsAsAssessed;
    }

    /* package */ void setCourse(Course course) {
        this.course = course;
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
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("gitHubName", gitHubName)
                .add("course", course.getId())
                .add("commissionsAsAssessor", commissionsAsAssessor)
                .add("commissionsAsAssessed", commissionsAsAssessed)
                .add("created", created)
                .add("updated", updated)
                .toString();
    }
}
