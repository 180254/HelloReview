package pl.p.lodz.iis.hr.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.p.lodz.iis.hr.models.forms.InputText;

@Repository
public interface InputTextRepository extends JpaRepository<InputText, Long> {

}
