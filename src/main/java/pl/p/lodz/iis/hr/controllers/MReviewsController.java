package pl.p.lodz.iis.hr.controllers;

import org.jetbrains.annotations.NonNls;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.pac4j.oauth.client.GitHubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.p.lodz.iis.hr.configuration.appconfig.AppConfig;
import pl.p.lodz.iis.hr.exceptions.GitHubCommunicationException;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;
import pl.p.lodz.iis.hr.models.courses.Course;
import pl.p.lodz.iis.hr.models.courses.Participant;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.models.response.ReviewResponse;
import pl.p.lodz.iis.hr.models.response.ReviewResponseStatus;
import pl.p.lodz.iis.hr.repositories.CourseRepository;
import pl.p.lodz.iis.hr.repositories.FormRepository;
import pl.p.lodz.iis.hr.repositories.ReviewRepository;
import pl.p.lodz.iis.hr.repositories.ReviewResponseRepository;
import pl.p.lodz.iis.hr.services.LocaleService;
import pl.p.lodz.iis.hr.utils.GitHubExecutor;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
class MReviewsController {

    @Autowired private ReviewRepository reviewRepository;
    @Autowired private ReviewResponseRepository reviewResponseRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private FormRepository formRepository;
    @Autowired private GitHubClient gitHubClient;
    @Autowired private AppConfig appConfig;
    @Autowired private GitHub gitHub;
    @Autowired private LocaleService localeService;

    @RequestMapping(
            value = "/m/reviews",
            method = RequestMethod.GET)
    @Transactional
    public String rList(Model model) {

        List<Review> reviews = reviewRepository.findAll();
        model.addAttribute("reviews", reviews);
        model.addAttribute("newButton", true);

        return "m-reviews";
    }

    @RequestMapping(
            value = "/m/reviews/{reviewID}",
            method = RequestMethod.GET)
    @Transactional
    public String rListOne(@PathVariable long reviewID,
                           Model model) {

        if (!reviewRepository.exists(reviewID)) {
            throw new ResourceNotFoundException();
        }

        Review review = reviewRepository.findOne(reviewID);
        model.addAttribute("reviews", Collections.singletonList(review));
        model.addAttribute("newButton", false);

        return "m-reviews";
    }

    @RequestMapping(
            value = "/m/reviews/for/course/{courseID}",
            method = RequestMethod.GET)
    @Transactional
    public String rListForCourse(@PathVariable long courseID,
                                 Model model) {

        if (!courseRepository.exists(courseID)) {
            throw new ResourceNotFoundException();
        }

        Course course = courseRepository.findOne(courseID);

        model.addAttribute("reviews", course.getReviews());
        model.addAttribute("newButton", false);

        return "m-reviews";
    }

    @RequestMapping(
            value = "/m/reviews/add",
            method = RequestMethod.GET)
    @Transactional
    public String rAdd(Model model) {

        List<Course> courses = courseRepository.findAll();
        List<Form> forms = formRepository.findByTemporaryFalse();

        model.addAttribute("courses", courses);
        model.addAttribute("forms", forms);
        return "m-reviews-add";
    }

    @RequestMapping(
            value = "/m/reviews/add/repolist",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> rAddRepoList(HttpServletResponse response) {
        List<String> repoList = new ArrayList<>(10);

        try {
            GitHubExecutor.<Void>ex(() -> {

                for (String username : appConfig.getGitHubConfig().getCourseRepos().getUserNames()) {
                    gitHub.getUser(username).listRepositories()
                            .asList().stream()
                            .map(ghRepo -> String.format("%s/%s", ghRepo.getOwnerName(), ghRepo.getName()))
                            .forEach(repoList::add);

                }

                return null;
            });

        } catch (GitHubCommunicationException e) {
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            return Collections.singletonList(e.toString());
        }

        return repoList;
    }

    @RequestMapping(
            value = "/m/reviews/add",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> mAddPOST(@NonNls @ModelAttribute("review-add-course") long courseID,
                                 @NonNls @ModelAttribute("review-add-form") long formID,
                                 @NonNls @ModelAttribute("review-add-repository") String repository,
                                 HttpServletResponse response) throws IOException {

        if (!courseRepository.exists(courseID)
                || !formRepository.exists(formID)
                || !repository.contains("/")) {

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return Collections.singletonList(localeService.getMessage("NoResource"));
        }

        GHRepository ghRepository;

        try {
            ghRepository = GitHubExecutor.ex(() -> gitHub.getRepository(repository));
        } catch (GitHubCommunicationException ignored) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return Collections.singletonList(localeService.getMessage("NoResource"));
        }

        try {
            Course course = courseRepository.getOne(courseID);
            Form form = formRepository.getOne(formID);
            List<GHRepository> forks = GitHubExecutor.ex(() -> ghRepository.listForks().asList());

            Map<String, GHRepository> forksMap = new HashMap<>(forks.size());
            forks.forEach(fork -> forksMap.put(fork.getOwnerName(), fork));

            Review review = new Review(course, form);
            List<ReviewResponse> reviewResponses = new ArrayList<>(course.getParticipants().size());

            for (Participant participant : course.getParticipants()) {
                ReviewResponse reviewResponse = new ReviewResponse(review, participant);

                reviewResponse.setStatus(forksMap.containsKey(participant.getGitHubName())
                        ? ReviewResponseStatus.NOT_FORKED
                        : ReviewResponseStatus.PROCESSING);

                reviewResponse.setGitHubUrl("XXXXX" + new Random().nextLong());
                reviewResponses.add(reviewResponse);
            }

            reviewRepository.save(review);
            reviewResponseRepository.save(reviewResponses);

            return Collections.singletonList(String.valueOf(review.getId()));
        } catch (GitHubCommunicationException e) {
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            return Collections.singletonList(e.toString());
        }
    }
}
