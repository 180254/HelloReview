package pl.p.lodz.iis.hr.models.courses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import pl.p.lodz.iis.hr.models.response.ReviewResponse;

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

    @Column(nullable = false, length = 255, unique = true)
    @JsonView
    private String gitHubName;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @JsonView
    private Course course;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
    @JsonView
    @JsonProperty("reviewResponse")
    private List<ReviewResponse> reviewResponses;

    @Column(nullable = false)
    @JsonView
    private final LocalDateTime created = LocalDateTime.now();

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
    @JsonView
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

    public List<ReviewResponse> getReviewResponses() {
        return reviewResponses;
    }

    void setReviewResponses(List<ReviewResponse> reviewResponses) {
        this.reviewResponses = reviewResponses;
    }

    void setCourse(Course course) {
        this.course = course;
    }

    public LocalDateTime getCreated() {
        return created;
    }
}
