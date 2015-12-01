package pl.p.lodz.iis.hr.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.fragment.DOMSelectorFragmentSpec;
import pl.p.lodz.iis.hr.configuration.Long2;
import pl.p.lodz.iis.hr.exceptions.ErrorPageException;
import pl.p.lodz.iis.hr.exceptions.GHCommunicationException;
import pl.p.lodz.iis.hr.exceptions.LocalizableErrorRestException;
import pl.p.lodz.iis.hr.models.courses.Commission;
import pl.p.lodz.iis.hr.models.courses.CommissionStatus;
import pl.p.lodz.iis.hr.models.courses.Participant;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.services.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Controller
class MCommissionsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MCommissionsController.class);

    private final TemplateEngine templateEngine;
    private final ResCommonService resCommonService;
    private final RepositoryProvider repositoryProvider;
    private final GHTaskScheduler ghTaskScheduler;
    private final GHReviewCreator ghReviewCreator;
    private final LocaleService localeService;

    @Autowired
    MCommissionsController(TemplateEngine templateEngine,
                           ResCommonService resCommonService,
                           RepositoryProvider repositoryProvider,
                           GHTaskScheduler ghTaskScheduler,
                           GHReviewCreator ghReviewCreator,
                           LocaleService localeService) {
        this.templateEngine = templateEngine;
        this.resCommonService = resCommonService;
        this.repositoryProvider = repositoryProvider;
        this.ghTaskScheduler = ghTaskScheduler;
        this.ghReviewCreator = ghReviewCreator;
        this.localeService = localeService;
    }

    @RequestMapping(
            value = "/m/reviews/{reviewID}/commissions",
            method = RequestMethod.GET)
    @Transactional
    public String list1(@PathVariable Long2 reviewID,
                        Model model)
            throws ErrorPageException {

        Review review = resCommonService.getOne(repositoryProvider.review(), reviewID.get());
        List<Commission> commissions = review.getCommissions();
        boolean retryButtonEnabled = ghTaskScheduler.shouldRetryButtonBeEnabled();

        model.addAttribute("review", review);
        model.addAttribute("commissions", commissions);
        model.addAttribute("retryButtonEnabled", retryButtonEnabled);
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

        Commission commission = resCommonService.getOne(repositoryProvider.commission(), commissionID.get());
        boolean retryButtonEnabled = ghTaskScheduler.shouldRetryButtonBeEnabled();

        model.addAttribute("commissions", Collections.singletonList(commission));
        model.addAttribute("retryButtonEnabled", retryButtonEnabled);
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

        Participant participant = resCommonService.getOne(repositoryProvider.participant(), participantID.get());
        List<Commission> commissions = participant.getCommissionsAsAssessor();
        boolean retryButtonEnabled = ghTaskScheduler.shouldRetryButtonBeEnabled();


        model.addAttribute("participant", participant);
        model.addAttribute("commissions", commissions);
        model.addAttribute("retryButtonEnabled", retryButtonEnabled);
        model.addAttribute("addon_forParticipant", true);

        return "m-commissions";
    }

    @RequestMapping(
            value = "/m/reviews/commissions/failed",
            method = RequestMethod.GET)
    @Transactional
    public String list4(Model model) {

        List<Commission> failed = repositoryProvider.commission().findByStatusIn(
                Arrays.asList(CommissionStatus.PROCESSING_FAILED, CommissionStatus.PROCESSING)
        );
        boolean retryButtonEnabled = ghTaskScheduler.shouldRetryButtonBeEnabled();

        model.addAttribute("commissions", failed);
        model.addAttribute("retryButtonEnabled", retryButtonEnabled);
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
                              HttpServletRequest request,
                              HttpServletResponse response,
                              ServletContext servletContext,
                              Locale locale)
            throws LocalizableErrorRestException {

        Commission comm = resCommonService.getOneForRest(repositoryProvider.commission(), commissionID.get());

        if ((comm.getStatus() != CommissionStatus.PROCESSING_FAILED)
                && !ghTaskScheduler.shouldRetryButtonBeEnabled()) {
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

        LOGGER.info("Commission retried {}", comm);
        comm.setStatus(CommissionStatus.PROCESSING);
        ghTaskScheduler.registerClone(comm);
        repositoryProvider.commission().save(comm);

        String msg = localeService.get("m.commissions.retry.done");

        WebContext ctx = new WebContext(request, response, servletContext, locale);
        ctx.setVariable("commissions", Collections.singletonList(comm));
        ctx.setVariable("retryButtonEnabled", false);
        String row = templateEngine.process("m-commissions", ctx, new DOMSelectorFragmentSpec(".commission-one"));

        return Arrays.asList(msg, row);
    }

}
