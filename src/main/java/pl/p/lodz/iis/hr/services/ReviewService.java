package pl.p.lodz.iis.hr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.models.courses.CommissionStatus;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.repositories.ReviewRepository;

import java.util.Collection;

/**
 * Util service to Review model.
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final GHTaskScheduler ghTaskScheduler;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository,
                         GHTaskScheduler ghTaskScheduler) {
        this.reviewRepository = reviewRepository;
        this.ghTaskScheduler = ghTaskScheduler;
    }

    public boolean canBeDeleted(Review review) {
        return review.getCommissions().stream()
                .allMatch(comm -> comm.getStatus() != CommissionStatus.PROCESSING);
    }

    public boolean canBeDeleted(Collection<Review> reviews) {
        return reviews.stream().allMatch(this::canBeDeleted);
    }

    public void delete(Review review) {
        review.getCommissions().stream()
                .filter(comm -> comm.getStatus().isCopyExistOnGitHub())
                .forEach(comm -> ghTaskScheduler.registerDelete(comm.getUuid().toString()));

        reviewRepository.delete(review);
    }

    public void delete(Collection<Review> reviews) {
        reviews.stream().forEach(this::delete);
    }

}
