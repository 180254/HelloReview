package pl.p.lodz.iis.hr.controllers;

import org.kohsuke.github.GHRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.p.lodz.iis.hr.configuration.Long2;
import pl.p.lodz.iis.hr.exceptions.GHCommunicationException;
import pl.p.lodz.iis.hr.exceptions.LocalizableErrorRestException;
import pl.p.lodz.iis.hr.exceptions.LocalizedErrorRestException;
import pl.p.lodz.iis.hr.exceptions.NotFoundException;
import pl.p.lodz.iis.hr.models.courses.Course;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.repositories.CourseRepository;
import pl.p.lodz.iis.hr.repositories.FormRepository;
import pl.p.lodz.iis.hr.repositories.ReviewRepository;
import pl.p.lodz.iis.hr.services.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
class MReviewsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MReviewsController.class);

    private final ResCommonService resCommonService;
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final FormRepository formRepository;
    private final ReviewService reviewService;
    private final LocaleService localeService;
    private final FieldValidator fieldValidator;
    private final GHReviewCreator ghReviewCreator;

    @Autowired
    MReviewsController(ResCommonService resCommonService,
                       ReviewRepository reviewRepository,
                       CourseRepository courseRepository,
                       FormRepository formRepository,
                       ReviewService reviewService,
                       LocaleService localeService,
                       FieldValidator fieldValidator,
                       GHReviewCreator ghReviewCreator) {
        this.resCommonService = resCommonService;
        this.reviewRepository = reviewRepository;
        this.courseRepository = courseRepository;
        this.formRepository = formRepository;
        this.reviewService = reviewService;
        this.localeService = localeService;
        this.fieldValidator = fieldValidator;
        this.ghReviewCreator = ghReviewCreator;
    }

    @RequestMapping(
            value = "/m/reviews",
            method = RequestMethod.GET)
    @Transactional
    public String list(Model model) {

        List<Review> reviews = reviewRepository.findAll();

        model.addAttribute("reviews", reviews);
        model.addAttribute("newButton", true);

        return "m-reviews";
    }

    @RequestMapping(
            value = "/m/reviews/{reviewID}",
            method = RequestMethod.GET)
    @Transactional
    public String listOne(@PathVariable Long2 reviewID,
                          Model model)
            throws NotFoundException {

        Review review = resCommonService.getOne(reviewRepository, reviewID.get());

        model.addAttribute("reviews", Collections.singletonList(review));
        model.addAttribute("newButton", false);
        model.addAttribute("addon_oneReview", true);

        return "m-reviews";
    }

    @RequestMapping(
            value = "/m/reviews/for/course/{courseID}",
            method = RequestMethod.GET)
    @Transactional
    public String listForCourse(@PathVariable Long2 courseID,
                                Model model)
            throws NotFoundException {

        Course course = resCommonService.getOne(courseRepository, courseID.get());
        List<Review> reviews = course.getReviews();

        model.addAttribute("course", course);
        model.addAttribute("reviews", reviews);
        model.addAttribute("newButton", false);
        model.addAttribute("addon_forCourse", true);

        return "m-reviews";
    }


    @RequestMapping(
            value = "/m/reviews/for/form/{formID}",
            method = RequestMethod.GET)
    @Transactional
    public String listForForm(@PathVariable Long2 formID,
                              Model model)
            throws NotFoundException {

        Form form = resCommonService.getOne(formRepository, formID.get());
        List<Review> reviews = form.getReviews();

        model.addAttribute("form", form);
        model.addAttribute("reviews", reviews);
        model.addAttribute("newButton", false);
        model.addAttribute("addon_forForm", true);

        return "m-reviews";
    }

    @RequestMapping(
            value = "/m/reviews/add",
            method = RequestMethod.GET)
    @Transactional
    public String kAdd(Model model) {

        List<Course> courses = courseRepository.findAll();
        List<Form> forms = formRepository.findByTemporaryFalse();

        model.addAttribute("f", new GHReviewAddForm());
        model.addAttribute("courses", courses);
        model.addAttribute("forms", forms);

        return "m-reviews-add";
    }

    @RequestMapping(
            value = "/m/reviews/add/repolist",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> kAddRepoList()
            throws LocalizedErrorRestException {

        try {
            return ghReviewCreator.getListOfCourseRepos();
        } catch (GHCommunicationException e) {
            throw (LocalizedErrorRestException)
                    new LocalizedErrorRestException(e.getMessage()).initCause(e);
        }
    }

    @RequestMapping(
            value = "/m/reviews/delete",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> delete(@ModelAttribute("id") Long2 reviewID)
            throws NotFoundException, LocalizableErrorRestException {

        Review review = resCommonService.getOne(reviewRepository, reviewID.get());

        if (!reviewService.canBeDeleted(review)) {
            throw new LocalizableErrorRestException("m.reviews.delete.cannot.as.comm.processing");
        }

        LOGGER.debug("Review deleted {}", review);
        reviewService.delete(review);

        return localeService.getAsList("m.reviews.delete.done");
    }

    @RequestMapping(
            value = "/m/reviews/openclose",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> openClose(@ModelAttribute("id") Long2 reviewID)
            throws NotFoundException {

        Review review = resCommonService.getOne(reviewRepository, reviewID.get());

        LOGGER.debug("Review closed state changes {} to {}", review, !review.isClosed());
        review.setClosed(!review.isClosed());
        reviewRepository.save(review);

        return review.isClosed() ? Arrays.asList(
                localeService.get("m.reviews.open.close.closed"),
                localeService.get("m.reviews.tbody.open.close.open")
        ) : Arrays.asList(
                localeService.get("m.reviews.open.close.opened"),
                localeService.get("m.reviews.tbody.open.close.close")
        );
    }


    @RequestMapping(
            value = "/m/reviews/rename",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> rename(@ModelAttribute("value") String newName,
                               @ModelAttribute("pk") Long2 reviewID)
            throws NotFoundException, LocalizedErrorRestException {

        Review review = resCommonService.getOne(reviewRepository, reviewID.get());

        fieldValidator.validateFieldRestEx(
                new Review(newName, 0L, null, null, null),
                "name",
                localeService.get("m.reviews.add.validation.prefix.name")
        );

        LOGGER.debug("Review {} renamed to {}", review, newName);
        review.setName(newName);
        reviewRepository.save(review);

        return localeService.getAsList("m.reviews.rename.done");
    }

    @RequestMapping(
            value = "/m/reviews/add",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> kAddPOST(@ModelAttribute("f") GHReviewAddForm ghReviewAddForm,
                                 BindingResult result, // prevent error if ghReviewAddForm not filled
                                 HttpServletResponse response)
            throws LocalizedErrorRestException, LocalizableErrorRestException {

        if (!courseRepository.exists(ghReviewAddForm.getCourseID())
                || !formRepository.exists(ghReviewAddForm.getFormID())
                || !ghReviewAddForm.getRepositoryFullName().contains("/")) {

            throw LocalizableErrorRestException.noResource();
        }

        fieldValidator.validateFieldsRestEx(
                new Review(ghReviewAddForm.getName(), ghReviewAddForm.getRespPerPeer(), null, null, ghReviewAddForm.getName()),
                new String[]{
                        "name",
                        "commPerPeer"
                }, new String[]{
                        localeService.get("m.reviews.add.validation.prefix.name"),
                        localeService.get("m.reviews.add.validation.prefix.comm.per.peer")
                }
        );


        GHRepository ghRepository;

        try {
            ghRepository = ghReviewCreator.getRepositoryByName(ghReviewAddForm.getRepositoryFullName());
        } catch (GHCommunicationException ignored) {
            throw LocalizableErrorRestException.noResource();
        }

        LOGGER.debug("Review creating {}", ghReviewAddForm);
        return ghReviewCreator.createReview(ghReviewAddForm, ghRepository, response);
    }
}

