package pl.p.lodz.iis.hr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;
import pl.p.lodz.iis.hr.models.courses.Course;
import pl.p.lodz.iis.hr.repositories.CourseRepository;
import pl.p.lodz.iis.hr.utils.ExceptionChecker;

@Controller
class MReviewController {

    @Autowired private CourseRepository courseRepository;

    @RequestMapping(
            value = "/m/courses/{courseID}/reviews",
            method = RequestMethod.GET)
    public String students(@PathVariable long courseID,
                           Model model) {

        Course curse = courseRepository.getOne(courseID);

        if (ExceptionChecker.checkExceptionThrown(curse::getId)) {
            throw new ResourceNotFoundException();
        }

        model.addAttribute("curse", curse);
        return "m-courses-student";
    }
}
