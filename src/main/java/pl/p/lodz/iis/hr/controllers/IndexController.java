package pl.p.lodz.iis.hr.controllers;

import org.pac4j.oauth.client.GitHubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.p.lodz.iis.hr.configuration.security.SecurityHelper;
import pl.p.lodz.iis.hr.xmlconfig.XMLConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class IndexController {

    @Autowired
    private GitHubClient gitHubClient;

    @Autowired
    private XMLConfig xmlConfig;

    @RequestMapping("/")
    public String index(HttpServletRequest request,
                        HttpServletResponse response,
                        Model model) {

        SecurityHelper securityHelper = new SecurityHelper(gitHubClient, request, response);

        model.addAttribute("isLoggedIn", securityHelper.isAuthenticated());
        if (securityHelper.isAuthenticated()) {
            model.addAttribute("isMaster", securityHelper.isMaster(xmlConfig));
            model.addAttribute("username", securityHelper.getUserProfileFromSession().getUsername());
        }

        return "index";
    }
}
