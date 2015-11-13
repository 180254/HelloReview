package pl.p.lodz.iis.hr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
class GitHubController {

    @RequestMapping(
            value = "/github-issue",
            method = RequestMethod.GET)
    public String githubIssue() {
        return "github-issue";
    }
}
