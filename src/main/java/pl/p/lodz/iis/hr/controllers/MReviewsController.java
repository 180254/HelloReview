package pl.p.lodz.iis.hr.controllers;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.p.lodz.iis.hr.configuration.appconfig.AppConfig;
import pl.p.lodz.iis.hr.configuration.long2.Long2;
import pl.p.lodz.iis.hr.exceptions.GitHubCommunicationException;
import pl.p.lodz.iis.hr.exceptions.InternalException;
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
import pl.p.lodz.iis.hr.services.GitCloneService;
import pl.p.lodz.iis.hr.services.LocaleService;
import pl.p.lodz.iis.hr.services.ValidateService;
import pl.p.lodz.iis.hr.utils.GitHubExecutor;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

@Controller
class MReviewsController {

    @Autowired private ReviewRepository reviewRepository;
    @Autowired private ReviewResponseRepository reviewResponseRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private FormRepository formRepository;
    @Autowired private AppConfig appConfig;
    @Autowired private GitHub gitHub;
    @Autowired private LocaleService localeService;
    @Autowired private ValidateService validateService;
    @Autowired private GitCloneService gitCloneService;

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
                          Model model) {

        if (!reviewRepository.exists(reviewID.get())) {
            throw new ResourceNotFoundException();
        }

        Review review = reviewRepository.findOne(reviewID.get());
        model.addAttribute("reviews", singletonList(review));
        model.addAttribute("newButton", false);

        return "m-reviews";
    }

    @RequestMapping(
            value = "/m/reviews/for/course/{courseID}",
            method = RequestMethod.GET)
    @Transactional
    public String listForCourse(@PathVariable Long2 courseID,
                                Model model) {

        if (!courseRepository.exists(courseID.get())) {
            throw new ResourceNotFoundException();
        }

        Course course = courseRepository.findOne(courseID.get());

        model.addAttribute("reviews", course.getReviews());
        model.addAttribute("newButton", false);

        return "m-reviews";
    }


    @RequestMapping(
            value = "/m/reviews/for/form/{formID}",
            method = RequestMethod.GET)
    @Transactional
    public String listForForm(@PathVariable Long2 formID,
                              Model model) {

        if (!formRepository.exists(formID.get())) {
            throw new ResourceNotFoundException();
        }

        Form form = formRepository.getOne(formID.get());

        model.addAttribute("reviews", form.getReviews());
        model.addAttribute("newButton", false);

        return "m-reviews";
    }

    @RequestMapping(
            value = "/m/reviews/add",
            method = RequestMethod.GET)
    @Transactional
    public String kAdd(Model model) {

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
    public List<String> kAddRepoList(HttpServletResponse response) {
        List<String> repoList = new ArrayList<>(10);

        try {
            GitHubExecutor.ex(() -> {

                for (String username : appConfig.getGitHubConfig().getCourseRepos().getUserNames()) {
                    gitHub.getUser(username).listRepositories()
                            .asList().stream()
                            .map(ghRepo -> String.format("%s/%s", ghRepo.getOwnerName(), ghRepo.getName()))
                            .forEach(repoList::add);

                }
            });

        } catch (GitHubCommunicationException e) {
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            return singletonList(e.toString());
        }

        return repoList;
    }

    @RequestMapping(
            value = "/m/reviews/delete",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> delete(@ModelAttribute("id") Long2 reviewID,
                               HttpServletResponse response) {

        if (!reviewRepository.exists(reviewID.get())) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.getMessage("NoResource"));
        }

        reviewRepository.delete(reviewID.get());
        return singletonList(localeService.getMessage("m.reviews.delete.done"));
    }

    @RequestMapping(
            value = "/m/reviews/rename",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> rename(@ModelAttribute("value") String newName,
                               @ModelAttribute("pk") Long2 reviewID,
                               HttpServletResponse response) {

        if (!reviewRepository.exists(reviewID.get())) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.getMessage("NoResource"));
        }

        List<String> nameErrors = validateService.validateField(
                new Review(newName, 0L, null, null),
                "name",
                localeService.getMessage("m.reviews.add.validation.prefix.name")
        );

        if (!nameErrors.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return nameErrors;
        }

        Review review = reviewRepository.getOne(reviewID.get());
        review.setName(newName);
        reviewRepository.save(review);

        return singletonList(localeService.getMessage("m.reviews.rename.done"));
    }

    @RequestMapping(
            value = "/m/reviews/add",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> kAddPOST(@RequestParam("review-add-name") String name,
                                 @RequestParam("review-add-resp-per-peer") Long2 respPerPeer,
                                 @RequestParam("review-add-course") Long2 courseID,
                                 @RequestParam("review-add-form") Long2 formID,
                                 @RequestParam("review-add-repository") String repository,
                                 @RequestParam("review-add-ignore-warning") Long2 ignoreWarning,
                                 HttpServletResponse response) {

        if (!courseRepository.exists(courseID.get())
                || !formRepository.exists(formID.get())
                || !repository.contains("/")) {

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.getMessage("NoResources"));
        }

        GHRepository ghRepository;

        try {
            ghRepository = GitHubExecutor.ex(() -> gitHub.getRepository(repository));
        } catch (GitHubCommunicationException ignored) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.getMessage("NoResources"));
        }

        List<String> errors = validateService.validateFields(
                new Review(name, respPerPeer.get(), null, null),
                new String[]{
                        "name",
                        "respPerPeer"
                }, new String[]{
                        localeService.getMessage("m.reviews.add.validation.prefix.name"),
                        localeService.getMessage("m.reviews.add.validation.prefix.resp.per.peer")
                }
        );

        if (!errors.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return errors;
        }

        // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // //

        try {
            Course course = courseRepository.getOne(courseID.get());
            List<Participant> participants = course.getParticipants();
            Form form = formRepository.getOne(formID.get());
            List<GHRepository> forks = GitHubExecutor.ex(() -> ghRepository.listForks().asList());

            Map<String, GHRepository> forksMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            forks.forEach(fork -> forksMap.put(fork.getOwnerName(), fork));

            List<Participant> participWhoForked = participants.stream()
                    .filter(p -> forksMap.containsKey(p.getGitHubName()))
                    .collect(Collectors.toList());

            List<Participant> participWhoNotForked = participants.stream()
                    .filter(p -> !forksMap.containsKey(p.getGitHubName()))
                    .collect(Collectors.toList());

            if ((ignoreWarning.get() == 0L) && !participWhoNotForked.isEmpty()) {
                response.setStatus(HttpStatus.PRECONDITION_FAILED.value());

                List<String> warning = new ArrayList<>(10);
                warning.add(String.valueOf(participWhoNotForked.size()));
                warning.add(String.valueOf(participants.size()));
                participWhoNotForked.forEach((p) -> warning.add(p.getName()));
                return warning;
            }

            long respPerPeer2 = Math.min((long) participWhoForked.size() - 1L, respPerPeer.get());
            if ((respPerPeer2 == 0L) && (participWhoForked.size() == 1)) {
                respPerPeer2 = 1L;
            }
            if (respPerPeer2 == -1L) {
                respPerPeer2 = 0L;
            }

            List<Participant> mulParticipants = new ArrayList<>(10);
            while (mulParticipants.size() < participants.size()) {
                mulParticipants.addAll(participWhoForked);
            }
            Collections.shuffle(mulParticipants);
            mulParticipants.addAll(participWhoForked);

            Review review = new Review(name, respPerPeer2, course, form);
            List<ReviewResponse> responses = new ArrayList<>(10);

            for (Participant particip : participWhoNotForked) {
                ReviewResponse rResponse = new ReviewResponse(review, particip, null, null);
                rResponse.setStatus(ReviewResponseStatus.NOT_FORKED);
                responses.add(rResponse);
            }

            for (Participant assessor : course.getParticipants()) {
                for (long lo = 0L; lo < respPerPeer2; lo++) {

                    Participant assessed = (participWhoForked.size() == 1)
                            ? pop(mulParticipants)
                            : popNotMe(mulParticipants, assessor);
                    GHRepository assessedRepo = forksMap.get(assessed.getGitHubName());

                    ReviewResponse rResponse =
                            new ReviewResponse(review, assessed, assessor, assessedRepo.getHtmlUrl().toString());
                    responses.add(rResponse);

                }
            }

            reviewRepository.save(review);
            reviewResponseRepository.save(responses);

            responses.stream()
                    .filter(r -> r.getStatus() != ReviewResponseStatus.NOT_FORKED)
                    .forEach(r -> gitCloneService.registerCloneJob(r, forksMap.get(r.getAssessed().getGitHubName())));

            return singletonList(String.valueOf(review.getId()));

        } catch (GitHubCommunicationException e) {
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            return singletonList(e.toString());
        }
    }

    public <T> T pop(List<T> collection) {
        T t = collection.get(0);
        collection.remove(t);
        return t;
    }

    public <T> T popNotMe(Collection<T> collection, T me) {
        for (T collElement : collection) {
            if (!me.equals(collElement)) {
                collection.remove(collElement);
                return collElement;
            }
        }
        throw new InternalException();
    }


}

