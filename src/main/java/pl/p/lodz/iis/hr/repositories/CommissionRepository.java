package pl.p.lodz.iis.hr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.p.lodz.iis.hr.models.courses.Commission;
import pl.p.lodz.iis.hr.models.courses.CommissionStatus;
import pl.p.lodz.iis.hr.models.courses.Participant;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {

    List<Commission> findByStatus(CommissionStatus status);

    List<Commission> findByStatusIn(List<CommissionStatus> statuses);

    List<Commission> findByAssessorIn(List<Participant> participants);

    Commission findByUuid(UUID uuid);
}
