package pl.p.lodz.iis.hr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.p.lodz.iis.hr.models.response.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
