package pl.p.lodz.iis.hr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
class GitHubIssueController {

    @RequestMapping(
            value = "/github-issue",
            method = RequestMethod.GET)
    public String githubIssue() {
        return "github-issue";
    }

    @RequestMapping(
            value = "/github-issue",
            method = RequestMethod.POST)
    public String githubIssuePOST(@ModelAttribute("message") String message,
                                  Model model) {

        model.addAttribute("message", message);
        return "github-issue";
    }
}
