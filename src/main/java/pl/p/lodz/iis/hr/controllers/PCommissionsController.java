package pl.p.lodz.iis.hr.controllers;

import org.pac4j.oauth.client.GitHubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.p.lodz.iis.hr.configuration.GHPac4jSecurityHelper;
import pl.p.lodz.iis.hr.models.courses.Commission;
import pl.p.lodz.iis.hr.models.courses.Participant;
import pl.p.lodz.iis.hr.repositories.CommissionRepository;
import pl.p.lodz.iis.hr.repositories.ParticipantRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class PCommissionsController {

    @Autowired private GitHubClient gitHubClient;
    @Autowired private ParticipantRepository participantRepository;
    @Autowired private CommissionRepository commissionRepository;

    @RequestMapping("/p/commissions")
    public String commissions(HttpServletRequest request,
                              HttpServletResponse response,
                              Model model) {

        GHPac4jSecurityHelper ghSecurityHelper = new GHPac4jSecurityHelper(gitHubClient, request, response);
        String username = ghSecurityHelper.getUserProfileFromSession().getUsername();

        List<Participant> byGitHubName = participantRepository.findByGitHubName(username);
        List<Commission> byAssessorIn = commissionRepository.findByAssessorIn(byGitHubName);

        model.addAttribute("commissions", byAssessorIn);
        return "p-commissions";
    }
}
