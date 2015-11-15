package pl.p.lodz.iis.hr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.p.lodz.iis.hr.models.courses.Commission;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {
}
