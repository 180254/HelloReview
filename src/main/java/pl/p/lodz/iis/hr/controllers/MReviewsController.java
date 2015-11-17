package pl.p.lodz.iis.hr.controllers;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
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
import pl.p.lodz.iis.hr.exceptions.GitHubCommunicationException;
import pl.p.lodz.iis.hr.exceptions.InternalException;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;
import pl.p.lodz.iis.hr.models.courses.*;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.repositories.*;
import pl.p.lodz.iis.hr.services.GitExecuteService;
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
    @Autowired private Review2Repository review2Repository;
    @Autowired private CommissionRepository commissionRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private FormRepository formRepository;
    @Autowired private AppConfig appConfig;
    @Autowired @Qualifier("ghFail") private GitHub gitHubFail;
    @Autowired private LocaleService localeService;
    @Autowired private ValidateService validateService;
    @Autowired private GitExecuteService gitExecuteService;

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

        model.addAttribute("addon_oneReview", true);


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

        model.addAttribute("course", course);
        model.addAttribute("reviews", course.getReviews());
        model.addAttribute("newButton", false);

        model.addAttribute("addon_forCourse", true);

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

        model.addAttribute("form", form);
        model.addAttribute("reviews", form.getReviews());
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
    public List<String> kAddRepoList(HttpServletResponse response) {
        List<String> repoList = new ArrayList<>(10);

        try {
            GitHubExecutor.ex(() -> {

                for (String username : appConfig.getGitHubConfig().getCourseRepos().getUserNames()) {
                    gitHubFail.getUser(username).listRepositories()
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
            return singletonList(localeService.get("NoResource"));
        }

        Review review = reviewRepository.findOne(reviewID.get());

        if (!review2Repository.canBeDeleted(review)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.get("m.reviews.delete.cannot.as.comm.processing"));
        }

        review2Repository.delete(review);
        return singletonList(localeService.get("m.reviews.delete.done"));
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
            return singletonList(localeService.get("NoResource"));
        }

        List<String> nameErrors = validateService.validateField(
                new Review(newName, 0L, null, null, null),
                "name",
                localeService.get("m.reviews.add.validation.prefix.name")
        );

        if (!nameErrors.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return nameErrors;
        }

        Review review = reviewRepository.getOne(reviewID.get());
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
                                 HttpServletResponse response) {

        if (!courseRepository.exists(courseID.get())
                || !formRepository.exists(formID.get())
                || !repository.contains("/")) {

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.get("NoResources"));
        }

        GHRepository ghRepository;

        try {
            ghRepository = GitHubExecutor.ex(() -> gitHubFail.getRepository(repository));
        } catch (GitHubCommunicationException ignored) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.get("NoResources"));
        }

        List<String> errors = validateService.validateFields(
                new Review(name, respPerPeer.get(), null, null, name),
                new String[]{
                        "name",
                        "commPerPeer"
                }, new String[]{
                        localeService.get("m.reviews.add.validation.prefix.name"),
                        localeService.get("m.reviews.add.validation.prefix.comm.per.peer")
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
                    .forEach(r -> gitExecuteService.registerCloneJob(r, forksMap.get(r.getAssessed().getGitHubName())));

            return singletonList(String.valueOf(review.getId()));

        } catch (GitHubCommunicationException e) {
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            return singletonList(
                    String.format("%s %s", localeService.get("NoGitHub"), e.toString())
            );
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

