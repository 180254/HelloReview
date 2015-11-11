package pl.p.lodz.iis.hr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.p.lodz.iis.hr.models.courses.Course;

public interface CourseRepository extends JpaRepository<Course, Long>,
        FindByNameProvider<Course> {

}
