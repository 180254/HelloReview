package pl.p.lodz.iis.hr.models.courses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import pl.p.lodz.iis.hr.repositories.CourseRepository;
import pl.p.lodz.iis.hr.services.UniqueName;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Course implements Serializable {

    private static final long serialVersionUID = 6153695117247362094L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView
    private long id;

    @Column(nullable = false, length = 255, unique = true)
    @NotBlank

    @Length(min = 1, max = 255)
    @UniqueName(service = CourseRepository.class, message = "{constraints.unique.name}")
    @JsonView
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "course", orphanRemoval = true)
    @JsonView
    @JsonProperty("student")
    private List<Participant> participants;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "course", orphanRemoval = true)
    @JsonView
    @JsonProperty("review")
    private List<Review> reviews;

    @Column(nullable = false)
    @JsonView
    private final LocalDateTime created = LocalDateTime.now();

    Course() {
    }

    public Course(String name) {
        this.name = name;
        participants = new ArrayList<>(10);
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

    public List<Participant> getParticipants() {
        return participants;
    }

    void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public LocalDateTime getCreated() {
        return created;
    }
}
