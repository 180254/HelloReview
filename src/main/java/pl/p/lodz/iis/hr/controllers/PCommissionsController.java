package pl.p.lodz.iis.hr.controllers;

import org.pac4j.oauth.client.GitHubClient;
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
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.repositories.CommissionRepository;
import pl.p.lodz.iis.hr.repositories.ParticipantRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@Controller
class PCommissionsController {

    @Autowired private GitHubClient gitHubClient;
    @Autowired private ParticipantRepository participantRepository;
    @Autowired private CommissionRepository commissionRepository;

    @RequestMapping(
            value = "/p/commissions",
            method = RequestMethod.GET)
    @Transactional
    public String commissions(HttpServletRequest request,
                              HttpServletResponse response,
                              Model model) {

        List<Participant> byGitHubName = meAsParticipant(request, response);
        List<Commission> byAssessorIn = commissionRepository.findByAssessorIn(byGitHubName);

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
            throws ErrorPageException {

        UUID uuid1 = getUuidFromString(uuid);

        Commission byUuid = commissionRepository.findByUuid(uuid1);
        if (byUuid == null) {
            throw new ErrorPageException(HttpServletResponse.SC_NOT_FOUND);
        }

        List<Participant> byGitHubName = meAsParticipant(request, response);
        if (!byGitHubName.contains(byUuid.getAssessor())) {
            throw new ErrorPageException(HttpServletResponse.SC_FORBIDDEN);
        }

        Form form = byUuid.getReview().getForm();

        model.addAttribute("commission", byUuid);
        model.addAttribute("form", form);

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
        return participantRepository.findByGitHubName(username);
    }
}
