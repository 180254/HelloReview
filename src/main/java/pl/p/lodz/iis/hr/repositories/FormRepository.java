package pl.p.lodz.iis.hr.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.p.lodz.iis.hr.models.forms.Form;

import java.util.List;

@Repository
@SuppressWarnings("InterfaceNeverImplemented")
public interface FormRepository extends JpaRepository<Form, Long> {

    List<Form> findByTemporaryTrue();

    List<Form> findByTemporaryFalse();
}
