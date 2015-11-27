package pl.p.lodz.iis.hr.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Nothing special, index must be displayable.<br/>
 * Also /github-issue is handled here.
 */
@Controller
class IndexController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(
            value = "/github-issue",
            method = RequestMethod.GET)
    public String gitHubIssue() {
        return "github-issue";
    }
}
