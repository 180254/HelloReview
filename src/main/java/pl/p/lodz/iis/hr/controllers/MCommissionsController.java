package pl.p.lodz.iis.hr.controllers;

import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.p.lodz.iis.hr.configuration.Long2;
import pl.p.lodz.iis.hr.exceptions.GHCommunicationException;
import pl.p.lodz.iis.hr.exceptions.OtherRestProcessingException;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;
import pl.p.lodz.iis.hr.models.courses.Commission;
import pl.p.lodz.iis.hr.models.courses.CommissionStatus;
import pl.p.lodz.iis.hr.models.courses.Participant;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.repositories.CommissionRepository;
import pl.p.lodz.iis.hr.repositories.ParticipantRepository;
import pl.p.lodz.iis.hr.repositories.ReviewRepository;
import pl.p.lodz.iis.hr.services.GHTaskScheduler;
import pl.p.lodz.iis.hr.services.LocaleService;
import pl.p.lodz.iis.hr.services.ResCommonService;
import pl.p.lodz.iis.hr.utils.GHExecutor;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;

@Controller
class MCommissionsController {

    @Autowired private ResCommonService resCommonService;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private CommissionRepository commissionRepository;
    @Autowired private ParticipantRepository participantRepository;
    @Autowired private LocaleService localeService;
    @Autowired private GHTaskScheduler ghTaskScheduler;
    @Autowired @Qualifier("ghFail") private GitHub gitHubFail;

    @RequestMapping(
            value = "/m/reviews/{reviewID}/commissions",
            method = RequestMethod.GET)
    @Transactional
    public String list1(@PathVariable Long2 reviewID,
                        Model model)
            throws ResourceNotFoundException {

        Review review = resCommonService.getOne(reviewRepository, reviewID.get());
        int notCompleted = ghTaskScheduler.getApproxNumberOfScheduledTasks();
        boolean retryButton = notCompleted == 0;
        List<Commission> commissions = review.getCommissions();

        model.addAttribute("retryButtonForProcessing", retryButton);
        model.addAttribute("commissions", commissions);
        model.addAttribute("review", review);
        model.addAttribute("addon_forReview", true);

        return "m-commissions";
    }

    @RequestMapping(
            value = "/m/reviews/commissions/{commissionID}",
            method = RequestMethod.GET)
    @Transactional
    public String list2(@PathVariable Long2 commissionID,
                        Model model)
            throws ResourceNotFoundException {

        Commission commission = resCommonService.getOne(commissionRepository, commissionID.get());
        int notCompleted = ghTaskScheduler.getApproxNumberOfScheduledTasks();
        boolean retryButton = notCompleted == 0;

        model.addAttribute("commissions", singletonList(commission));
        model.addAttribute("retryButtonForProcessing", retryButton);
        model.addAttribute("addon_oneCommission", true);

        return "m-commissions";
    }

    @RequestMapping(
            value = "/m/reviews/commissions/for/participant/{participantID}",
            method = RequestMethod.GET)
    @Transactional
    public String list3(@PathVariable Long2 participantID,
                        Model model)
            throws ResourceNotFoundException {

        Participant participant = resCommonService.getOne(participantRepository, participantID.get());
        List<Commission> commissions = participant.getCommissions();
        int notCompleted = ghTaskScheduler.getApproxNumberOfScheduledTasks();
        boolean retryButton = notCompleted == 0;

        model.addAttribute("participant", participant);
        model.addAttribute("commissions", commissions);
        model.addAttribute("retryButtonForProcessing", retryButton);
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
        int notCompleted = ghTaskScheduler.getApproxNumberOfScheduledTasks();
        boolean retryButton = notCompleted == 0;

        model.addAttribute("commissions", failed);
        model.addAttribute("retryButtonForProcessing", retryButton);
        model.addAttribute("addon_failed", true);

        return "m-commissions";
    }

    @RequestMapping(
            value = "/m/reviews/commissions/retry",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> retry(@ModelAttribute("commission-id") Long2 commissionID)
            throws ResourceNotFoundException, OtherRestProcessingException {

        Commission comm = resCommonService.getOne(commissionRepository, commissionID.get());
        int notCompleted = ghTaskScheduler.getApproxNumberOfScheduledTasks();

        if ((comm.getStatus() != CommissionStatus.PROCESSING_FAILED) && (notCompleted != 0)) {
            throw new OtherRestProcessingException("BadResource");
        }

        try {
            GHExecutor.ex(() -> gitHubFail.getRepository(comm.getReview().getRepository()));
            ghTaskScheduler.registerClone(comm);

        } catch (GHCommunicationException e) {
            throw (OtherRestProcessingException)
                    new OtherRestProcessingException("NoGitHub", new Object[]{e.toString()})
                            .setStatusCode(HttpServletResponse.SC_SERVICE_UNAVAILABLE)
                            .initCause(e);

        }

        comm.setStatus(CommissionStatus.PROCESSING);
        commissionRepository.save(comm);

        return singletonList(localeService.get("m.commissions.retry.done"));
    }

}
