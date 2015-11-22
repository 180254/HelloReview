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
import pl.p.lodz.iis.hr.exceptions.GHCommunicationException;
import pl.p.lodz.iis.hr.exceptions.LocalizableErrorRestException;
import pl.p.lodz.iis.hr.models.courses.Commission;
import pl.p.lodz.iis.hr.models.courses.CommissionStatus;
import pl.p.lodz.iis.hr.models.courses.Participant;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.repositories.CommissionRepository;
import pl.p.lodz.iis.hr.repositories.ParticipantRepository;
import pl.p.lodz.iis.hr.repositories.ReviewRepository;
import pl.p.lodz.iis.hr.services.GHReviewCreator;
import pl.p.lodz.iis.hr.services.GHTaskScheduler;
import pl.p.lodz.iis.hr.services.LocaleService;
import pl.p.lodz.iis.hr.services.ResCommonService;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
class MCommissionsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MCommissionsController.class);

    private final ResCommonService resCommonService;
    private final ReviewRepository reviewRepository;
    private final CommissionRepository commissionRepository;
    private final ParticipantRepository participantRepository;
    private final LocaleService localeService;
    private final GHTaskScheduler ghTaskScheduler;
    private final GHReviewCreator ghReviewCreator;

    @Autowired
    MCommissionsController(ResCommonService resCommonService,
                           ReviewRepository reviewRepository,
                           CommissionRepository commissionRepository,
                           ParticipantRepository partiRepository,
                           LocaleService localeService,
                           GHTaskScheduler ghTaskScheduler,
                           GHReviewCreator ghReviewCreator) {
        this.resCommonService = resCommonService;
        this.reviewRepository = reviewRepository;
        this.commissionRepository = commissionRepository;
        this.participantRepository = partiRepository;
        this.localeService = localeService;
        this.ghTaskScheduler = ghTaskScheduler;
        this.ghReviewCreator = ghReviewCreator;
    }

    @RequestMapping(
            value = "/m/reviews/{reviewID}/commissions",
            method = RequestMethod.GET)
    @Transactional
    public String list1(@PathVariable Long2 reviewID,
                        Model model)
            throws ErrorPageException {

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
            throws ErrorPageException {

        Commission commission = resCommonService.getOne(commissionRepository, commissionID.get());
        int notCompleted = ghTaskScheduler.getApproxNumberOfScheduledTasks();
        boolean retryButton = notCompleted == 0;

        model.addAttribute("commissions", Collections.singletonList(commission));
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
            throws ErrorPageException {

        Participant participant = resCommonService.getOne(participantRepository, participantID.get());
        List<Commission> commissions = participant.getCommissionsAsAssessor();
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
            throws LocalizableErrorRestException {

        Commission comm = resCommonService.getOneForRest(commissionRepository, commissionID.get());
        int notCompleted = ghTaskScheduler.getApproxNumberOfScheduledTasks();

        if ((comm.getStatus() != CommissionStatus.PROCESSING_FAILED) && (notCompleted != 0)) {
            throw LocalizableErrorRestException.badResource();
        }

        try {
            ghReviewCreator.getRepositoryByName(comm.getReview().getRepository());

        } catch (GHCommunicationException e) {
            throw (LocalizableErrorRestException)
                    new LocalizableErrorRestException(
                            HttpServletResponse.SC_SERVICE_UNAVAILABLE, "NoGitHub", e.toString()
                    ).initCause(e);

        }

        LOGGER.debug("Commission retried {}", comm);
        comm.setStatus(CommissionStatus.PROCESSING);
        commissionRepository.save(comm);
        ghTaskScheduler.registerClone(comm);

        return localeService.getAsList("m.commissions.retry.done");
    }

}
