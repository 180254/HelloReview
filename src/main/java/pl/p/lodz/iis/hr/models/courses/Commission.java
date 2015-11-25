package pl.p.lodz.iis.hr.models.courses;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import pl.p.lodz.iis.hr.models.response.Response;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
public class Commission implements Serializable {

    private static final long serialVersionUID = 7967942829146568225L;
    private static final Pattern DESC_PROJECT = Pattern.compile("\\{project\\}");
    private static final Pattern DESC_URL = Pattern.compile("\\{url\\}");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView
    private long id;

    @Column(nullable = false, unique = true)
    @JsonView
    private UUID uuid;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(nullable = false)
    @Fetch(FetchMode.JOIN)
    @JsonView
    private Review review;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(nullable = false)
    @Fetch(FetchMode.JOIN)
    @JsonView
    private Participant assessed;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(nullable = true)
    @Fetch(FetchMode.JOIN)
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

    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(nullable = true)
    @Fetch(FetchMode.JOIN)
    @JsonView
    private Response response;

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

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getFilledFormDescription() {
        String desc = review.getForm().getDescription();
        desc = DESC_PROJECT.matcher(desc).replaceAll(Matcher.quoteReplacement(review.getRepository()));
        desc = DESC_URL.matcher(desc).replaceAll(Matcher.quoteReplacement(ghUrl));
        return desc;
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if ((obj == null) || !(obj instanceof Commission)) return false;
        Commission that = (Commission) obj;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
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
