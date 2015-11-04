package pl.p.lodz.iis.hr.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.p.lodz.iis.hr.models.forms.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

}
