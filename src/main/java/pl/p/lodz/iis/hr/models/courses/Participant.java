package pl.p.lodz.iis.hr.models.courses;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Participant implements Serializable {

    private static final long serialVersionUID = -6918665306614378792L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView
    private long id;

    @Column(nullable = false, length = 255, unique = true)
    private String name;

    @Column(nullable = false, length = 255, unique = true)
    @JsonView
    private String gitHubName;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @JsonView
    private Course course;

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

    void setGitHubName(String gitHubName) {
        this.gitHubName = gitHubName;
    }

    public Course getCourse() {
        return course;
    }

    void setCourse(Course course) {
        this.course = course;
    }

    public LocalDateTime getCreated() {
        return created;
    }
}
