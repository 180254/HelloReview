package pl.p.lodz.iis.hr.controllers;

import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
class IndexController {

    @Autowired private GitHub gitHub;

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET)
    public String index() {
        return "index";
    }
}
