package pl.p.lodz.iis.hr.models.courses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
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
    private LocalDateTime created;

    @Column(nullable = false)
    @JsonView
    private LocalDateTime updated;

    Course() {
    }

    public Course(String name) {
        this.name = name;
        participants = new ArrayList<>(10);
    }

    public long getId() {
        return id;
    }

    @NotBlank
    @Length(min = 1, max = 255)
    @UniqueName(service = CourseRepository.class)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    /* package */ void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    /* package */ void setReviews(List<Review> reviews) {
        this.reviews = reviews;
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
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
