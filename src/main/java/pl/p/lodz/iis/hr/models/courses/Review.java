package pl.p.lodz.iis.hr.models.courses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.models.response.ReviewResponse;

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

    public Review(Course course, Form form) {
        this.course = course;
        this.form = form;
        reviewResponses = new ArrayList<>(10);
    }

    public long getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    void setCourse(Course course) {
        this.course = course;
    }

    public Form getForm() {
        return form;
    }

    void setForm(Form form) {
        this.form = form;
    }

    public List<ReviewResponse> getReviewResponses() {
        return reviewResponses;
    }

    void setReviewResponses(List<ReviewResponse> reviewResponses) {
        this.reviewResponses = reviewResponses;
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
}
