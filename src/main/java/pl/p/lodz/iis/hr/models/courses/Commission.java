package pl.p.lodz.iis.hr.models.courses;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.MoreObjects;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
public class Commission implements Serializable {

    private static final long serialVersionUID = 7967942829146568225L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView
    private long id;

    @Column(nullable = false, unique = true)
    @JsonView
    private UUID uuid;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @JsonView
    private Review review;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @JsonView
    private Participant assessed;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(nullable = true)
    @JsonView
    private Participant assessor;

    @Column(nullable = true, length = 255, unique = false)
    @JsonView
    private String assessedGhUrl;

    @Column(nullable = true, length = 255, unique = true)
    @JsonView
    private String ghUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JsonView
    private CommissionStatus status;

    @Column(nullable = false)
    @JsonView
    private LocalDateTime created;

    @Column(nullable = false)
    @JsonView
    private LocalDateTime updated;

    Commission() {
    }

    public Commission(Review review, Participant assessed, Participant assessor, URL assessedGhUrl) {
        this(review, assessed, assessor, assessedGhUrl.toString());
    }

    public Commission(Review review, Participant assessed, Participant assessor, String assessedGhUrl) {
        uuid = UUID.randomUUID();
        this.review = review;
        this.assessed = assessed;
        this.assessor = assessor;
        this.assessedGhUrl = assessedGhUrl;
        status = CommissionStatus.PROCESSING;
    }

    public long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUuidShotVersion() {
        return String.format("%s...", uuid.toString().substring(0, 14));
    }

    /* package */ void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Review getReview() {
        return review;
    }

    /* package */ void setReview(Review review) {
        this.review = review;
    }

    public Participant getAssessed() {
        return assessed;
    }

    /* package */ void setAssessed(Participant assessed) {
        this.assessed = assessed;
    }

    public Participant getAssessor() {
        return assessor;
    }

    /* package */ void setAssessor(Participant assessor) {
        this.assessor = assessor;
    }

    @NotBlank
    @Length(min = 1, max = 255)
    public String getAssessedGhUrl() {
        return assessedGhUrl;
    }

    /* package */ void setAssessedGhUrl(String assessedGhUrl) {
        this.assessedGhUrl = assessedGhUrl;
    }

    @NotBlank
    @Length(min = 1, max = 255)
    public String getGhUrl() {
        return ghUrl;
    }

    public void setGhUrl(String ghUrl) {
        this.ghUrl = ghUrl;
    }

    @NotNull
    public CommissionStatus getStatus() {
        return status;
    }

    public void setStatus(CommissionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public String getCreatedDate() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(created);
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
                .add("uuid", uuid)
                .add("review", review.getId())
                .add("assessed", assessed.getId())
                .add("assessor", (assessor == null) ? "null" : assessor.getId())
                .add("assessedGhUrl", assessedGhUrl)
                .add("ghUrl", ghUrl)
                .add("status", status)
                .add("created", created)
                .add("updated", updated)
                .toString();
    }
}
