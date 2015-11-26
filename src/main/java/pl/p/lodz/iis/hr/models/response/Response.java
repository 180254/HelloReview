package pl.p.lodz.iis.hr.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import pl.p.lodz.iis.hr.models.RelationsAware;
import pl.p.lodz.iis.hr.models.courses.Commission;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Response implements Serializable, RelationsAware {

    private static final long serialVersionUID = -8893726458275377148L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView
    private long id;

    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(nullable = false)
    @Fetch(FetchMode.JOIN)
    @JsonView
    private Commission commission;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "response", orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    @JsonView
    @JsonProperty("review")
    private List<Answer> answers;

    @Column(nullable = false)
    @JsonView
    private LocalDateTime created;

    Response() {
    }

    public long getId() {
        return id;
    }

    public Response(Commission commission) {
        this.commission = commission;
        this.answers = new ArrayList<>(10);
    }

    public Commission getCommission() {
        return commission;
    }

    /* package */ void setCommission(Commission commission) {
        this.commission = commission;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    /* package */ void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    /* package */ void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if ((obj == null) || !(obj instanceof Response)) return false;
        Response that = (Response) obj;
        return commission.getId() == that.commission.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(commission.getId());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("commission", commission.getId())
                .add("answers", answers)
                .add("created", created)
                .toString();
    }

    @Override
    public void fixRelations() {
        for (Answer answer : answers) {
            answer.setResponse(this);
        }
    }
}