package pl.p.lodz.iis.hr.controllers;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.p.lodz.iis.hr.appconfig.AppConfig;
import pl.p.lodz.iis.hr.configuration.Long2;
import pl.p.lodz.iis.hr.exceptions.*;
import pl.p.lodz.iis.hr.models.courses.*;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.repositories.CommissionRepository;
import pl.p.lodz.iis.hr.repositories.CourseRepository;
import pl.p.lodz.iis.hr.repositories.FormRepository;
import pl.p.lodz.iis.hr.repositories.ReviewRepository;
import pl.p.lodz.iis.hr.services.*;
import pl.p.lodz.iis.hr.utils.GHExecutor;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

@Controller
class MReviewsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MReviewsController.class);

    @Autowired private ResCommonService resCommonService;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private ReviewService reviewService;
    @Autowired private CommissionRepository commissionRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private FormRepository formRepository;
    @Autowired private AppConfig appConfig;
    @Autowired @Qualifier("ghFail") private GitHub gitHubFail;
    @Autowired private LocaleService localeService;
    @Autowired private FieldValidator fieldValidator;
    @Autowired private GHTaskScheduler ghTaskScheduler;

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
            throws ResourceNotFoundException {

        Review review = resCommonService.getOne(reviewRepository, reviewID.get());

        model.addAttribute("reviews", singletonList(review));
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
            throws ResourceNotFoundException {

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
            throws ResourceNotFoundException {

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

        model.addAttribute("courses", courses);
        model.addAttribute("forms", forms);

        return "m-reviews-add";
    }

    @RequestMapping(
            value = "/m/reviews/add/repolist",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> kAddRepoList() throws FieldValidationRestException {

        List<String> repoList = new ArrayList<>(10);

        try {
            GHExecutor.ex(() -> {
                for (String username : appConfig.getGitHubConfig().getCourseRepos().getUserNames()) {
                    gitHubFail.getUser(username).listRepositories()
                            .asList().stream()
                            .map(ghRepo -> String.format("%s/%s", ghRepo.getOwnerName(), ghRepo.getName()))
                            .forEach(repoList::add);

                }
            });

        } catch (GHCommunicationException e) {
            throw (FieldValidationRestException)
                    new FieldValidationRestException(e.getMessage()).initCause(e);
        }

        return repoList;
    }

    @RequestMapping(
            value = "/m/reviews/delete",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> delete(@ModelAttribute("id") Long2 reviewID)
            throws ResourceNotFoundException, OtherRestProcessingException {

        Review review = resCommonService.getOne(reviewRepository, reviewID.get());

        if (!reviewService.canBeDeleted(review)) {
            throw new OtherRestProcessingException("m.reviews.delete.cannot.as.comm.processing");
        }

        LOGGER.debug("Review deleted: {}", review);
        reviewService.delete(review);

        return singletonList(localeService.get("m.reviews.delete.done"));
    }

    @RequestMapping(
            value = "/m/reviews/openclose",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> openClose(@ModelAttribute("id") Long2 reviewID)
            throws ResourceNotFoundException {

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
            throws ResourceNotFoundException, FieldValidationRestException {

        Review review = resCommonService.getOne(reviewRepository, reviewID.get());

        fieldValidator.validateFieldRestEx(
                new Review(newName, 0L, null, null, null),
                "name",
                localeService.get("m.reviews.add.validation.prefix.name")
        );

        LOGGER.debug("Review {} renamed to {}", review, newName);
        review.setName(newName);
        reviewRepository.save(review);

        return singletonList(localeService.get("m.reviews.rename.done"));
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
                                 HttpServletResponse response)
            throws FieldValidationRestException, ResourceNotFoundRestException, OtherRestProcessingException {

        if (!courseRepository.exists(courseID.get())
                || !formRepository.exists(formID.get())
                || !repository.contains("/")) {

            throw new ResourceNotFoundRestException();
        }

        GHRepository ghRepository;

        try {
            ghRepository = GHExecutor.ex(() -> gitHubFail.getRepository(repository));
        } catch (GHCommunicationException ignored) {
            throw new ResourceNotFoundRestException();
        }

        fieldValidator.validateFieldsdRestEx(
                new Review(name, respPerPeer.get(), null, null, name),
                new String[]{
                        "name",
                        "commPerPeer"
                }, new String[]{
                        localeService.get("m.reviews.add.validation.prefix.name"),
                        localeService.get("m.reviews.add.validation.prefix.comm.per.peer")
                }
        );


        // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // //

        try {
            Course course = courseRepository.getOne(courseID.get());
            List<Participant> participants = course.getParticipants();
            Form form = formRepository.getOne(formID.get());
            List<GHRepository> forks = GHExecutor.ex(() -> ghRepository.listForks().asList());

            Map<String, GHRepository> forksMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            forks.forEach(fork -> forksMap.put(fork.getOwnerName(), fork));

            List<Participant> participantWhoForked = participants.stream()
                    .filter(p -> forksMap.containsKey(p.getGitHubName()))
                    .collect(Collectors.toList());

            List<Participant> participantsWhoNotForked = participants.stream()
                    .filter(p -> !forksMap.containsKey(p.getGitHubName()))
                    .collect(Collectors.toList());

            long respPerPeer2 = Math.max(Math.min((long) participantWhoForked.size() - 1L, respPerPeer.get()), 0L);

            boolean preconditionFailed = (!participantsWhoNotForked.isEmpty() || (respPerPeer.get() != respPerPeer2));
            if ((ignoreWarning.get() == 0L) /*&& preconditionFailed*/) {
                response.setStatus(HttpStatus.PRECONDITION_FAILED.value());

                List<String> warning = new ArrayList<>(10);
                warning.add(String.valueOf(respPerPeer2));
                warning.add(String.valueOf(participantsWhoNotForked.size()));
                warning.add(String.valueOf(participants.size()));
                participantsWhoNotForked.forEach((p) -> warning.add(p.getName()));
                return warning;
            }


            List<Participant> mulParticipants = new ArrayList<>(10);
            long expectedMulParSize = (participants.size() * respPerPeer2);
            while (mulParticipants.size() < expectedMulParSize) {
                mulParticipants.addAll(participantWhoForked);
            }
            Collections.shuffle(mulParticipants);
            mulParticipants.addAll(participantWhoForked); // to be sure, that is enough

            Review review = new Review(name, respPerPeer2, course, form, repository);
            Collection<Commission> responses = new ArrayList<>(10);

            for (Participant participant : participantsWhoNotForked) {
                Commission rResponse = new Commission(review, participant, null, (String) null);
                rResponse.setStatus(CommissionStatus.NOT_FORKED);
                responses.add(rResponse);
            }

            Collection<Participant> assessedCollection = new LinkedList<>();

            for (Participant assessor : course.getParticipants()) {
                assessedCollection.clear();

                for (long lo = 0L; lo < respPerPeer2; lo++) {

                    Participant assessed = popUnique(mulParticipants, assessor, assessedCollection);
                    assessedCollection.add(assessed);

                    GHRepository assessedRepo = forksMap.get(assessed.getGitHubName());

                    Commission rResponse = new Commission(review, assessed, assessor, assessedRepo.getHtmlUrl());
                    responses.add(rResponse);

                }
            }

            reviewRepository.save(review);
            commissionRepository.save(responses);

            responses.stream()
                    .filter(r -> r.getStatus() != CommissionStatus.NOT_FORKED)
                    .forEach(r -> ghTaskScheduler.registerClone(r));

            return singletonList(String.valueOf(review.getId()));

        } catch (GHCommunicationException e) {
            throw (OtherRestProcessingException)
                    new OtherRestProcessingException("NoGitHub", new Object[]{e.toString()}).initCause(e);
        }
    }

    public <T> T popUnique(Collection<T> collection, T meExclude, Collection<T> excludeCollection) {
        for (T collElement : collection) {

            if (!meExclude.equals(collElement) && !excludeCollection.contains(collElement)) {
                collection.remove(collElement);
                return collElement;
            }

        }
        throw new InternalException();
    }


}

