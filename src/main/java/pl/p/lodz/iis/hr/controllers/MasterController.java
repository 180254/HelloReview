package pl.p.lodz.iis.hr.controllers;

import org.pac4j.oauth.client.GitHubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.p.lodz.iis.hr.xmlconfig.XMLConfig;

@Controller
public class MasterController {

    @Autowired private GitHubClient gitHubClient;
    @Autowired private XMLConfig xmlConfig;

    @RequestMapping("/m")
    public String index() {

        return "m-index";
    }
}
