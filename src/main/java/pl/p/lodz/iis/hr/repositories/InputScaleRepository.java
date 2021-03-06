package pl.p.lodz.iis.hr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.p.lodz.iis.hr.models.forms.InputScale;

@Repository
public interface InputScaleRepository extends JpaRepository<InputScale, Long> {

}
