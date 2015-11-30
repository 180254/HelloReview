package pl.p.lodz.iis.hr.models.courses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.repositories.ReviewRepository;
import pl.p.lodz.iis.hr.services.UniqueName;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private long commPerPeer;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(nullable = false)
    @Fetch(FetchMode.JOIN)
    @JsonView
    private Course course;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(nullable = false)
    @Fetch(FetchMode.JOIN)
    @JsonView
    private Form form;

    @Column(nullable = false, length = 255)
    @JsonView
    private String repository;

    @OneToMany(cascade = {}, fetch = FetchType.EAGER, mappedBy = "review", orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    @JsonView
    @JsonProperty("commission")
    private List<Commission> commissions;

    @Column(nullable = false)
    @JsonView
    private boolean closed;

    @Column(nullable = false)
    @JsonView
    private boolean cleaned;

    @Column(nullable = false)
    @JsonView
    private LocalDateTime created;

    @Column(nullable = false)
    @JsonView
    private LocalDateTime updated;

    Review() {
    }

    public Review(String name, long commPerPeer, Course course, Form form, String repository) {
        this.name = name;
        this.commPerPeer = commPerPeer;
        this.course = course;
        this.form = form;
        this.repository = repository;
        commissions = new ArrayList<>(10);
        closed = false;
        cleaned = false;
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
    public long getCommPerPeer() {
        return commPerPeer;
    }

    /* package */ void setCommPerPeer(long commPerPeer) {
        this.commPerPeer = commPerPeer;
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

    public String getRepository() {
        return repository;
    }

    /* package */ void setRepository(String repository) {
        this.repository = repository;
    }

    public List<Commission> getCommissions() {
        return commissions;
    }

    /* package */ void setCommissions(List<Commission> commissions) {
        this.commissions = commissions;
    }


    public long getNumberOfCommissionsToBeFilled() {
        return commissions.stream()
                .filter(commission -> commission.getStatus().isAvailableForPeer())
                .count();
    }

    public long getNumberOfFilledCommissions() {
        return commissions.stream()
                .filter(commission -> commission.getStatus() == CommissionStatus.FILLED)
                .count();
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isCleaned() {
        return cleaned;
    }

    public void setCleaned(boolean cleaned) {
        this.cleaned = cleaned;
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if ((obj == null) || !(obj instanceof Review)) return false;
        Review that = (Review) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("commPerPeer", commPerPeer)
                .add("course", course.getId())
                .add("form", form.getId())
                .add("repository", repository)
                .add("commissions", commissions)
                .add("closed", closed)
                .add("created", created)
                .add("updated", updated)
                .toString();
    }
}
