package pl.p.lodz.iis.hr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.p.lodz.iis.hr.models.courses.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, FindByNameProvider<Course> {

}
