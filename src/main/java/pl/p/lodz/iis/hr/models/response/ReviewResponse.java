package pl.p.lodz.iis.hr.models.response;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import pl.p.lodz.iis.hr.models.courses.Participant;
import pl.p.lodz.iis.hr.models.courses.Review;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class ReviewResponse implements Serializable {

    private static final long serialVersionUID = 7967942829146568225L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView
    private long id;

    @Column(nullable = false, unique = true)
    @JsonView
    private final UUID uuid = UUID.randomUUID();

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @JsonView
    private Review review;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @JsonView
    private Participant participant;

    @Column(nullable = false, length = 255, unique = true)
    @JsonView
    private String gitHubUrl;

    @Column(nullable = false)
    private ReviewResponseStatus status;

    @Column(nullable = false)
    @JsonView
    private LocalDateTime created;

    @Column(nullable = false)
    @JsonView
    private LocalDateTime updated;

    ReviewResponse() {
    }

    public ReviewResponse(Review review, Participant participant) {
        this.review = review;
        this.participant = participant;
        status = ReviewResponseStatus.PROCESSING;
    }

    public long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Review getReview() {
        return review;
    }

    void setReview(Review review) {
        this.review = review;
    }

    public Participant getParticipant() {
        return participant;
    }

    void setParticipant(Participant participant) {
        this.participant = participant;
    }

    @NotBlank
    @Length(min = 1, max = 255)
    public String getGitHubUrl() {
        return gitHubUrl;
    }

    public void setGitHubUrl(String gitHubUrl) {
        this.gitHubUrl = gitHubUrl;
    }

    @NotNull
    public ReviewResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewResponseStatus status) {
        this.status = status;
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
