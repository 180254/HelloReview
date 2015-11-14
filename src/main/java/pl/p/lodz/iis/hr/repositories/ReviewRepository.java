package pl.p.lodz.iis.hr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.p.lodz.iis.hr.models.courses.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, FindByNameProvider<Review> {
}
