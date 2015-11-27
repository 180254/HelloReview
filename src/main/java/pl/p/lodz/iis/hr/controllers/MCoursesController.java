package pl.p.lodz.iis.hr.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.p.lodz.iis.hr.configuration.Long2;
import pl.p.lodz.iis.hr.exceptions.ErrorPageException;
import pl.p.lodz.iis.hr.exceptions.LocalizableErrorRestException;
import pl.p.lodz.iis.hr.exceptions.LocalizedErrorRestException;
import pl.p.lodz.iis.hr.models.courses.Course;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.services.*;

import java.util.Collections;
import java.util.List;

@Controller
class MCoursesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MCoursesController.class);

    private final ResCommonService resCommonService;
    private final RepositoryProvider repositoryProvider;
    private final ReviewService reviewService;
    private final FieldValidator fieldValidator;
    private final LocaleService localeService;

    @Autowired
    MCoursesController(ResCommonService resCommonService,
                       RepositoryProvider repositoryProvider,
                       ReviewService reviewService,
                       FieldValidator fieldValidator,
                       LocaleService localeService) {
        this.resCommonService = resCommonService;
        this.repositoryProvider = repositoryProvider;
        this.reviewService = reviewService;
        this.fieldValidator = fieldValidator;
        this.localeService = localeService;
    }

    @RequestMapping(
            value = "/m/courses",
            method = RequestMethod.GET)
    @Transactional
    public String list(Model model) {

        List<Course> courses = repositoryProvider.course().findAll();

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
            throws ErrorPageException {

        Course course = resCommonService.getOne(repositoryProvider.course(), courseID.get());

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
            throws LocalizedErrorRestException {

        Course course = new Course(courseName);

        fieldValidator.validateFieldRestEx(
                course,
                "name",
                localeService.get("m.courses.add.validation.prefix.course.name")
        );

        LOGGER.debug("Course adding {}", course);
        repositoryProvider.course().save(course);
        LOGGER.debug("Added course ID {}", course.getId());

        return localeService.getAsList("m.courses.add.done");
    }

    @RequestMapping(
            value = "/m/courses/delete",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> delete(@ModelAttribute("id") Long2 courseID)
            throws LocalizableErrorRestException {

        Course course = resCommonService.getOneForRest(repositoryProvider.course(), courseID.get());
        List<Review> reviews = course.getReviews();

        if (!reviewService.canBeDeleted(reviews)) {
            throw new LocalizableErrorRestException("m.reviews.delete.cannot.as.comm.processing");
        }

        LOGGER.debug("Course deleted {}", course);
        reviewService.delete(reviews);
        repositoryProvider.course().delete(courseID.get());

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
            throws LocalizedErrorRestException, LocalizableErrorRestException {

        Course course = resCommonService.getOneForRest(repositoryProvider.course(), courseID.get());

        fieldValidator.validateFieldRestEx(
                new Course(newName),
                "name",
                localeService.get("m.courses.add.validation.prefix.course.name")
        );

        LOGGER.debug("Course renamed {} to {}", course, newName);
        course.setName(newName);
        repositoryProvider.course().save(course);

        return localeService.getAsList("m.courses.rename.done");
    }
}
