package pl.p.lodz.iis.hr.controllers;

import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.p.lodz.iis.hr.exceptions.GitHubCommunicationException;

import java.io.IOException;

@Controller
class MStatsController {

    @Autowired private GitHub gitHub;

    @RequestMapping(
            value = "/m/stats",
            method = RequestMethod.GET)
    public String stats(Model model) {
        try {
            GHRateLimit rateLimit = gitHub.getRateLimit();
            model.addAttribute("rateLimit", rateLimit);

            return "m-stats";

        } catch (IOException e) {
            throw new GitHubCommunicationException(e);
        }
    }

    @ExceptionHandler(GitHubCommunicationException.class)
    public String handleGitHubException() {
        return "redirect:/github-issue";
    }
}
