package pl.p.lodz.iis.hr.controllers;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.client.GitHubClient;
import org.pac4j.oauth.profile.github.GitHubProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.p.lodz.iis.hr.services.XMLConfigProvider;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class SampleController {

    @Autowired
    XMLConfigProvider xmlConfigProviderService;
    @Autowired private ServletContext servletContext;
//
//    @Autowired
//    ProfileManager profileManager;

    @RequestMapping("/")
    public String main(HttpServletRequest request,HttpServletResponse response, Model model) throws RequiresHttpAction {

        return "main";
    }

    @RequestMapping("/auth/callback")
    public String login() {

        return "main";
    }

    @RequestMapping("/oauth/github")
    public String logi1n() {

        return "main";
    }
}


