package pl.p.lodz.iis.hr.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.models.courses.CommissionStatus;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.services.GitExecuteService;

import java.util.Collection;

@Service
public class Review2Repository {

    @Autowired private ReviewRepository reviewRepository;
    @Autowired private GitExecuteService gitExecuteService;


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
                .forEach(comm -> gitExecuteService.registerDelete(comm.getUuid().toString()));

        reviewRepository.delete(review);
    }

    public void delete(Collection<Review> reviews) {
        reviews.stream().forEach(this::delete);
    }

}
