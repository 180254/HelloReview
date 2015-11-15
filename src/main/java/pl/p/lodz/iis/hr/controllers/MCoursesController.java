package pl.p.lodz.iis.hr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.p.lodz.iis.hr.configuration.long2.Long2;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;
import pl.p.lodz.iis.hr.models.courses.Course;
import pl.p.lodz.iis.hr.repositories.CourseRepository;
import pl.p.lodz.iis.hr.services.LocaleService;
import pl.p.lodz.iis.hr.services.ValidateService;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@Controller
class MCoursesController {

    @Autowired private CourseRepository courseRepository;
    @Autowired private LocaleService localeService;
    @Autowired private ValidateService validateService;

    @RequestMapping(
            value = "/m/courses",
            method = RequestMethod.GET)
    @Transactional
    public String list(Model model) {

        List<Course> courses = courseRepository.findAll();
        model.addAttribute("courses", courses);
        model.addAttribute("newButton", true);

        return "m-courses";
    }

    @RequestMapping(
            value = "/m/courses/{courseID}",
            method = RequestMethod.GET)
    @Transactional
    public String listOne(@PathVariable Long2 courseID,
                          Model model) {

        if (!courseRepository.exists(courseID.get())) {
            throw new ResourceNotFoundException();
        }

        Course course = courseRepository.findOne(courseID.get());
        model.addAttribute("courses", Collections.singletonList(course));
        model.addAttribute("newButton", false);

        return "m-courses";
    }

    @RequestMapping(
            value = "/m/courses/add",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> kAddPost(@ModelAttribute("course-name") String courseName,
                                 HttpServletResponse response) {

        Course course = new Course(courseName);

        List<String> errors = validateService.validateField(
                course,
                "name",
                localeService.getMessage("m.courses.add.validation.prefix.course.name")
        );

        if (!errors.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return errors;
        }

        courseRepository.save(course);
        return Collections.singletonList(localeService.getMessage("m.courses.add.done"));
    }

    @RequestMapping(
            value = "/m/courses/delete",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> delete(@ModelAttribute("id") Long2 courseID,
                               HttpServletResponse response) {

        if (!courseRepository.exists(courseID.get())) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return Collections.singletonList(localeService.getMessage("NoResource"));
        }

        courseRepository.delete(courseID.get());
        return Collections.singletonList(localeService.getMessage("m.courses.delete.done"));
    }

    @RequestMapping(
            value = "/m/courses/rename",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> rename(@ModelAttribute("value") String newName,
                               @ModelAttribute("pk") Long2 courseID,
                               HttpServletResponse response) {

        if (!courseRepository.exists(courseID.get())) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return Collections.singletonList(localeService.getMessage("NoResource"));
        }

        List<String> errors = validateService.validateField(
                new Course(newName),
                "name",
                localeService.getMessage("m.courses.add.validation.prefix.course.name")
        );

        if (!errors.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return errors;
        }

        Course course = courseRepository.getOne(courseID.get());
        course.setName(newName);
        courseRepository.save(course);

        return Collections.singletonList(localeService.getMessage("m.courses.rename.done"));
    }

}
