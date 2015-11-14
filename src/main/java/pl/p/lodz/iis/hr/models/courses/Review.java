package pl.p.lodz.iis.hr.models.courses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.models.response.ReviewResponse;
import pl.p.lodz.iis.hr.repositories.ReviewRepository;
import pl.p.lodz.iis.hr.services.UniqueName;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Review implements Serializable {

    private static final long serialVersionUID = 3899515500994387467L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView
    private long id;

    @Column(nullable = false, length = 255)
    @JsonView
    private String name;

    @Column(nullable = false)
    @JsonView
    private long respPerPeer;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @JsonView
    private Course course;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @JsonView
    private Form form;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "review", orphanRemoval = true)
    @JsonView
    @JsonProperty("reviewreponse")
    private List<ReviewResponse> reviewResponses;

    @Column(nullable = false)
    @JsonView
    private LocalDateTime created;

    @Column(nullable = false)
    @JsonView
    private LocalDateTime updated;

    Review() {
    }

    public Review(String name, long respPerPeer, Course course, Form form) {
        this.name = name;
        this.respPerPeer = respPerPeer;
        this.course = course;
        this.form = form;
        reviewResponses = new ArrayList<>(10);
    }

    public long getId() {
        return id;
    }

    @NotBlank
    @Length(min = 1, max = 255)
    @UniqueName(service = ReviewRepository.class)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Range(min = 1, max = 10)
    public long getRespPerPeer() {
        return respPerPeer;
    }

    /* package */ void setRespPerPeer(long respPerPeer) {
        this.respPerPeer = respPerPeer;
    }

    public Course getCourse() {
        return course;
    }

    /* package */ void setCourse(Course course) {
        this.course = course;
    }

    public Form getForm() {
        return form;
    }

    /* package */ void setForm(Form form) {
        this.form = form;
    }

    public List<ReviewResponse> getReviewResponses() {
        return reviewResponses;
    }

    /* package */ void setReviewResponses(List<ReviewResponse> reviewResponses) {
        this.reviewResponses = reviewResponses;
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
}
