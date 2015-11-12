package pl.p.lodz.iis.hr.controllers;

import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;
import pl.p.lodz.iis.hr.models.courses.Course;
import pl.p.lodz.iis.hr.repositories.CourseRepository;
import pl.p.lodz.iis.hr.services.LocaleService;
import pl.p.lodz.iis.hr.services.ValidateService;
import pl.p.lodz.iis.hr.utils.ExceptionChecker;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
class MCoursesController {

    @Autowired private CourseRepository courseRepository;
    @Autowired private LocaleService localeService;
    @Autowired private ValidateService validateService;

    @RequestMapping(
            value = "/m/courses",
            method = RequestMethod.GET)
    public String cList(Model model) {
        List<Course> courses = courseRepository.findAll();
        model.addAttribute("courses", courses);
        return "m-courses";
    }

    @RequestMapping(
            value = "/m/courses/{courseID}/participants",
            method = RequestMethod.GET)
    public String participants(@PathVariable long courseID,
                               Model model) {

        Course curse = courseRepository.getOne(courseID);

        if (ExceptionChecker.checkExceptionThrown(curse::getId)) {
            throw new ResourceNotFoundException();
        }

        model.addAttribute("curse", curse);
        return "m-courses-participants";
    }

    @RequestMapping(
            value = "/m/courses/add",
            method = RequestMethod.POST)
    @Transactional
    @ResponseBody
    public Object fCoursesAddPOST(@NonNls @ModelAttribute("course-name") String courseName,
                                  HttpServletResponse response) throws IOException {

        Course course = new Course(courseName);
        List<String> nameErrors = validateService.validateField(course, "name");

        if (!nameErrors.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return nameErrors;
        }

        courseRepository.save(course);
        return localeService.getMessage("m.courses.add.done");

    }

    @RequestMapping(
            value = "/m/courses/delete/{courseID}",
            method = RequestMethod.POST)
    @Transactional
    @ResponseBody
    public String delete(@PathVariable long courseID,
                         HttpServletResponse response) {

        Course course = courseRepository.getOne(courseID);

        if (ExceptionChecker.checkExceptionThrown(course::getId)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return localeService.getMessage("NoResource");
        }

        courseRepository.delete(course);
        return localeService.getMessage("m.courses.delete.done");
    }

    @RequestMapping(
            value = "/m/courses/rename",
            method = RequestMethod.POST)
    @Transactional
    @ResponseBody
    public Object rename(@NonNls @ModelAttribute("value") String newName,
                         @NonNls @ModelAttribute("pk") long courseID,
                         HttpServletResponse response) {

        Course course = courseRepository.getOne(courseID);

        if (ExceptionChecker.checkExceptionThrown(course::getId)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return localeService.getMessage("NoResource");
        }

        Course testCourse = new Course(newName);
        List<String> nameErrors = validateService.validateField(testCourse, "name");
        if (!nameErrors.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return nameErrors;
        }

        course.setName(newName);
        courseRepository.save(course);
        return localeService.getMessage("m.courses.rename.done");
    }

}
