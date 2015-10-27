package pl.p.lodz.iis.hr.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.models.forms.questions.InputScale;

@Repository
public interface InputScaleRepository extends JpaRepository<InputScale, Long>{

}
