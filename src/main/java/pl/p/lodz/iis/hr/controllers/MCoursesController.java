package pl.p.lodz.iis.hr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.p.lodz.iis.hr.configuration.Long2;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;
import pl.p.lodz.iis.hr.models.courses.Course;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.repositories.CourseRepository;
import pl.p.lodz.iis.hr.repositories.Review2Repository;
import pl.p.lodz.iis.hr.services.FieldValidateService;
import pl.p.lodz.iis.hr.services.LocaleService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static java.util.Collections.singletonList;

@Controller
class MCoursesController {

    @Autowired private CourseRepository courseRepository;
    @Autowired private LocaleService localeService;
    @Autowired private FieldValidateService fieldValidateService;
    @Autowired private Review2Repository review2Repository;

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
        model.addAttribute("courses", singletonList(course));
        model.addAttribute("newButton", false);

        model.addAttribute("addon_oneCourse", true);

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

        List<String> errors = fieldValidateService.validateField(
                course,
                "name",
                localeService.get("m.courses.add.validation.prefix.course.name")
        );

        if (!errors.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return errors;
        }

        courseRepository.save(course);
        return singletonList(localeService.get("m.courses.add.done"));
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
            return singletonList(localeService.get("NoResource"));
        }

        Course course = courseRepository.getOne(courseID.get());

        List<Review> reviews = course.getReviews();
        if (!review2Repository.canBeDeleted(reviews)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.get("m.reviews.delete.cannot.as.comm.processing"));
        }

        review2Repository.delete(reviews);
        courseRepository.delete(courseID.get());
        return singletonList(localeService.get("m.courses.delete.done"));
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
            return singletonList(localeService.get("NoResource"));
        }

        List<String> errors = fieldValidateService.validateField(
                new Course(newName),
                "name",
                localeService.get("m.courses.add.validation.prefix.course.name")
        );

        if (!errors.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return errors;
        }

        Course course = courseRepository.getOne(courseID.get());
        course.setName(newName);
        courseRepository.save(course);

        return singletonList(localeService.get("m.courses.rename.done"));
    }

}
