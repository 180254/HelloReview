package pl.p.lodz.iis.hr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;
import pl.p.lodz.iis.hr.models.courses.Course;
import pl.p.lodz.iis.hr.repositories.CourseRepository;
import pl.p.lodz.iis.hr.utils.ExceptionChecker;

@Controller
class MCoursesParticipantsController {

    @Autowired private CourseRepository courseRepository;

    @RequestMapping(
            value = "/m/courses/{courseID}/participants",
            method = RequestMethod.GET)
    @Transactional
    public String participants(@PathVariable long courseID,
                               Model model) {

        Course course = courseRepository.getOne(courseID);

        if (ExceptionChecker.checkExceptionThrown(course::getId)) {
            throw new ResourceNotFoundException();
        }

        model.addAttribute("course", course);
        model.addAttribute("participants", course.getParticipants());
        return "m-courses-participants";
    }
}
