package pl.p.lodz.iis.hr.models.response;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.models.courses.Student;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
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
    private UUID uuid = UUID.randomUUID();

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @JsonView
    private Review review;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @JsonView
    private Student student;

    @Column(nullable = false, length = 255, unique = true)
    @JsonView
    @NotBlank
    @Length(min = 1, max = 255)
    private String gitHubUrl;

    @Column(nullable = false)
    @NotNull
    private ReviewResponseStatus status;

    ReviewResponse() {
    }

    public ReviewResponse(Review review, Student student) {
        this.review = review;
        this.student = student;
        status = ReviewResponseStatus.CREATING;
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

    public void setReview(Review review) {
        this.review = review;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getGitHubUrl() {
        return gitHubUrl;
    }

    public void setGitHubUrl(String gitHubUrl) {
        this.gitHubUrl = gitHubUrl;
    }

    public ReviewResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewResponseStatus status) {
        this.status = status;
    }
}
