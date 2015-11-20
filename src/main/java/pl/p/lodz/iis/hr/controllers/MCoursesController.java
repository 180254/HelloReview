package pl.p.lodz.iis.hr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.p.lodz.iis.hr.configuration.Long2;
import pl.p.lodz.iis.hr.exceptions.FieldValidationRestException;
import pl.p.lodz.iis.hr.exceptions.OtherRestProcessingException;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundRestException;
import pl.p.lodz.iis.hr.models.courses.Course;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.repositories.CourseRepository;
import pl.p.lodz.iis.hr.services.FieldValidator;
import pl.p.lodz.iis.hr.services.LocaleService;
import pl.p.lodz.iis.hr.services.ResCommonService;
import pl.p.lodz.iis.hr.services.ReviewService;

import java.util.Collections;
import java.util.List;

@Controller
class MCoursesController {

    private final ResCommonService resCommonService;
    private final CourseRepository courseRepository;
    private final ReviewService reviewService;
    private final FieldValidator fieldValidator;
    private final LocaleService localeService;

    @Autowired
    MCoursesController(ResCommonService resCommonService,
                       CourseRepository courseRepository,
                       ReviewService reviewService,
                       FieldValidator fieldValidator,
                       LocaleService localeService) {
        this.resCommonService = resCommonService;
        this.courseRepository = courseRepository;
        this.reviewService = reviewService;
        this.fieldValidator = fieldValidator;
        this.localeService = localeService;
    }

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
                          Model model)
            throws ResourceNotFoundException {

        Course course = resCommonService.getOne(courseRepository, courseID.get());

        model.addAttribute("courses", Collections.singletonList(course));
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
    public List<String> kAddPost(@ModelAttribute("course-name") String courseName)
            throws FieldValidationRestException {

        Course course = new Course(courseName);

        fieldValidator.validateFieldRestEx(
                course,
                "name",
                localeService.get("m.courses.add.validation.prefix.course.name")
        );

        courseRepository.save(course);

        return localeService.getAsList("m.courses.add.done");
    }

    @RequestMapping(
            value = "/m/courses/delete",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> delete(@ModelAttribute("id") Long2 courseID)
            throws ResourceNotFoundRestException, OtherRestProcessingException {

        Course course = resCommonService.getOneForRest(courseRepository, courseID.get());

        List<Review> reviews = course.getReviews();
        if (!reviewService.canBeDeleted(reviews)) {
            throw new OtherRestProcessingException("m.reviews.delete.cannot.as.comm.processing");
        }

        reviewService.delete(reviews);
        courseRepository.delete(courseID.get());

        return localeService.getAsList("m.courses.delete.done");
    }

    @RequestMapping(
            value = "/m/courses/rename",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> rename(@ModelAttribute("value") String newName,
                               @ModelAttribute("pk") Long2 courseID)
            throws FieldValidationRestException, ResourceNotFoundRestException {

        Course course = resCommonService.getOneForRest(courseRepository, courseID.get());

        fieldValidator.validateFieldRestEx(
                new Course(newName),
                "name",
                localeService.get("m.courses.add.validation.prefix.course.name")
        );

        course.setName(newName);
        courseRepository.save(course);

        return localeService.getAsList("m.courses.rename.done");
    }
}
