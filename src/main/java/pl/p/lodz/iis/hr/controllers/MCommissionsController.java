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
import pl.p.lodz.iis.hr.configuration.long2.Long2;
import pl.p.lodz.iis.hr.exceptions.GitHubCommunicationException;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;
import pl.p.lodz.iis.hr.models.courses.Commission;
import pl.p.lodz.iis.hr.models.courses.CommissionStatus;
import pl.p.lodz.iis.hr.models.courses.Participant;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.repositories.CommissionRepository;
import pl.p.lodz.iis.hr.repositories.ParticipantRepository;
import pl.p.lodz.iis.hr.repositories.ReviewRepository;
import pl.p.lodz.iis.hr.services.GitExecuteService;
import pl.p.lodz.iis.hr.services.LocaleService;
import pl.p.lodz.iis.hr.utils.GitHubExecutor;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
class MCommissionsController {

    @Autowired private ReviewRepository reviewRepository;
    @Autowired private CommissionRepository commissionRepository;
    @Autowired private ParticipantRepository participantRepository;
    @Autowired private LocaleService localeService;
    @Autowired private GitExecuteService gitExecuteService;
    @Autowired @Qualifier("gitHubFail") private GitHub gitHubFail;

    @RequestMapping(
            value = "/m/reviews/{reviewID}/commissions",
            method = RequestMethod.GET)
    @Transactional
    public String list1(@PathVariable Long2 reviewID,
                        Model model) {

        if (!reviewRepository.exists(reviewID.get())) {
            throw new ResourceNotFoundException();
        }

        Review review = reviewRepository.findOne(reviewID.get());
        int notCompleted = gitExecuteService.getApproxNumberOfNotCompletedTasks();
        model.addAttribute("retryButtonForProcessing", notCompleted == 0);

        model.addAttribute("commissions", review.getCommissions());
        model.addAttribute("review", review);

        model.addAttribute("addon_forReview", true);


        return "m-commissions";
    }

    @RequestMapping(
            value = "/m/reviews/commissions/{commissionID}",
            method = RequestMethod.GET)
    @Transactional
    public String list2(@PathVariable Long2 commissionID,
                        Model model) {

        if (!commissionRepository.exists(commissionID.get())) {
            throw new ResourceNotFoundException();
        }

        Commission commission = commissionRepository.findOne(commissionID.get());
        model.addAttribute("commissions", Collections.singletonList(commission));
        int notCompleted = gitExecuteService.getApproxNumberOfNotCompletedTasks();
        model.addAttribute("retryButtonForProcessing", notCompleted == 0);

        model.addAttribute("addon_oneCommission", true);

        return "m-commissions";
    }

    @RequestMapping(
            value = "/m/reviews/commissions/for/participant/{participantID}",
            method = RequestMethod.GET)
    @Transactional
    public String list3(@PathVariable Long2 participantID,
                        Model model) {

        if (!participantRepository.exists(participantID.get())) {
            throw new ResourceNotFoundException();
        }

        Participant participant = participantRepository.findOne(participantID.get());
        int notCompleted = gitExecuteService.getApproxNumberOfNotCompletedTasks();

        model.addAttribute("participant", participant);
        model.addAttribute("commissions", participant.getCommissions());
        model.addAttribute("retryButtonForProcessing", notCompleted == 0);

        model.addAttribute("addon_forParticipant", true);

        return "m-commissions";
    }

    @RequestMapping(
            value = "/m/reviews/commissions/failed",
            method = RequestMethod.GET)
    @Transactional
    public String list4(Model model) {

        List<Commission> failed = commissionRepository.findByStatusIn(
                Arrays.asList(CommissionStatus.PROCESSING_FAILED, CommissionStatus.PROCESSING)
        );
        int notCompleted = gitExecuteService.getApproxNumberOfNotCompletedTasks();

        model.addAttribute("commissions", failed);
        model.addAttribute("retryButtonForProcessing", notCompleted == 0);

        model.addAttribute("addon_failed", true);

        return "m-commissions";
    }

    @RequestMapping(
            value = "/m/reviews/commissions/retry",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> retry(@ModelAttribute("commission-id") Long2 commissionID,
                              HttpServletResponse response) {

        if (!commissionRepository.exists(commissionID.get())) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return Collections.singletonList(localeService.get("NoResource"));
        }

        Commission comm = commissionRepository.getOne(commissionID.get());
        int notCompleted = gitExecuteService.getApproxNumberOfNotCompletedTasks();

        if ((comm.getStatus() != CommissionStatus.PROCESSING_FAILED) && (notCompleted != 0)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return Collections.singletonList(localeService.get("BadResource"));
        }

        try {
            GHRepository repo = GitHubExecutor.ex(() -> gitHubFail.getRepository(comm.getReview().getRepository()));
            gitExecuteService.registerCloneJob(comm, repo);

        } catch (GitHubCommunicationException e) {
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            return Collections.singletonList(
                    String.format("%s %s", localeService.get("NoGitHub"), e.toString())
            );
        }

        comm.setStatus(CommissionStatus.PROCESSING);
        commissionRepository.save(comm);
        return Collections.singletonList(localeService.get("m.commissions.retry.done"));
    }

}
