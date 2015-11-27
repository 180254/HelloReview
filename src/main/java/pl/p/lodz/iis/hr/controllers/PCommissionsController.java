package pl.p.lodz.iis.hr.controllers;

import org.pac4j.oauth.client.GitHubClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.p.lodz.iis.hr.configuration.GHPac4jSecurityHelper;
import pl.p.lodz.iis.hr.exceptions.ErrorPageException;
import pl.p.lodz.iis.hr.models.courses.Commission;
import pl.p.lodz.iis.hr.models.courses.Participant;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.services.AnswerProvider;
import pl.p.lodz.iis.hr.services.ProxyService;
import pl.p.lodz.iis.hr.services.RepositoryProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@Controller
class PCommissionsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PCommissionsController.class);

    private final GitHubClient gitHubClient;
    private final RepositoryProvider repositoryProvider;
    private final ProxyService proxyService;

    @Autowired
    PCommissionsController(GitHubClient gitHubClient,
                           RepositoryProvider repositoryProvider,
                           ProxyService proxyService) {
        this.gitHubClient = gitHubClient;
        this.repositoryProvider = repositoryProvider;
        this.proxyService = proxyService;
    }

    @RequestMapping(
            value = "/p/commissions",
            method = RequestMethod.GET)
    @Transactional
    public String commissions(HttpServletRequest request,
                              HttpServletResponse response,
                              Model model) {

        List<Participant> byGitHubName = meAsParticipant(request, response);
        List<Commission> byAssessorIn = repositoryProvider.commission().findByAssessorIn(byGitHubName);

        model.addAttribute("commissions", byAssessorIn);
        return "p-commissions";
    }

    @RequestMapping(
            value = "/p/commissions/{uuid}",
            method = RequestMethod.GET)
    @Transactional
    public String form(@PathVariable String uuid,
                       HttpServletRequest request,
                       HttpServletResponse response,
                       Model model)
            throws ErrorPageException, InterruptedException {

        UUID uuid1 = getUuidFromString(uuid);

        Commission commission = repositoryProvider.commission().findByUuid(uuid1);
        if (commission == null) {
            throw new ErrorPageException(HttpServletResponse.SC_NOT_FOUND);
        }

        List<Participant> byGitHubName = meAsParticipant(request, response);
        if (!byGitHubName.contains(commission.getAssessor())) {
            throw new ErrorPageException(HttpServletResponse.SC_FORBIDDEN);
        }

        if (!commission.getStatus().isAvailableForPeer()) {
            return "p-form-not-yet";
        }

        Review review = commission.getReview();
        Form form = review.getForm();

        AnswerProvider answerProvider = new AnswerProvider(commission.getResponse());

        model.addAttribute("form", form);
        model.addAttribute("review", review);
        model.addAttribute("commission", commission);
        model.addAttribute("answerProvider", answerProvider);
        model.addAttribute("proxyService", proxyService);

        return "p-form";
    }

    private UUID getUuidFromString(String uuid) throws ErrorPageException {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw (ErrorPageException) new ErrorPageException(HttpServletResponse.SC_NOT_FOUND).initCause(e);
        }
    }

    private List<Participant> meAsParticipant(HttpServletRequest request, HttpServletResponse response) {
        GHPac4jSecurityHelper ghSecurityHelper = new GHPac4jSecurityHelper(gitHubClient, request, response);
        String username = ghSecurityHelper.getUserProfileFromSession().getUsername();
        return repositoryProvider.participant().findByGitHubName(username);
    }
}
