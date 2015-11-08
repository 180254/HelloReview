package pl.p.lodz.iis.hr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.p.lodz.iis.hr.models.response.ReviewResponse;

@Repository
public interface ReviewResponseRepository extends JpaRepository<ReviewResponse, Long> {
}
