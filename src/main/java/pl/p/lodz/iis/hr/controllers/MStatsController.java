package pl.p.lodz.iis.hr.controllers;

import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.p.lodz.iis.hr.exceptions.GitHubCommunicationException;
import pl.p.lodz.iis.hr.services.GitExecuteService;
import pl.p.lodz.iis.hr.utils.GitHubExecutor;

@Controller
class MStatsController {

    @Autowired private GitHub gitHub;
    @Autowired private GitExecuteService gitExecuteService;

    @RequestMapping(
            value = "/m/stats",
            method = RequestMethod.GET)
    public String stats(Model model) {

        try {

            GitHubExecutor.ex(() -> {
                GHRateLimit rateLimit = gitHub.getRateLimit();
                model.addAttribute("rateLimit", rateLimit);
            });

        } catch (GitHubCommunicationException e) {
            model.addAttribute("rateLimit", e);
        }

        model.addAttribute("submitted", gitExecuteService.getApproxNumberOfSubmittedTasks());
        model.addAttribute("notCompleted", gitExecuteService.getApproxNumberOfNotCompletedTasks());

        return "m-stats";

    }
}
