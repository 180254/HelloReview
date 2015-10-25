package pl.p.lodz.iis.hr.controllers;

import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.oauth.client.GitHubClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.p.lodz.iis.hr.configuration.security.SecurityHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static pl.p.lodz.iis.hr.utils.ExceptionChecker.checkNoExceptionThrown;

@Controller
public class AuthController {
    /**
     * /login  - pl.p.lodz.iis.hr.controllers.AuthController
     * /logout - org.pac4j.springframework.web.ApplicationLogoutController
     * /callback - org.pac4j.springframework.web.CallbackController
     */

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private GitHubClient gitHubClient;

    @RequestMapping("/login")
    public String login(HttpServletRequest request,
                        HttpServletResponse response) {

        SecurityHelper securityHelper = new SecurityHelper(gitHubClient, request, response);

        if (securityHelper.getProfileManager().isAuthenticated()
                && checkNoExceptionThrown(securityHelper::getUserProfileUp2Date)) {
            return "redirect:/";

        } else {
            securityHelper.getProfileManager().remove(true);

            try {
                return String.format("redirect:%s", securityHelper.getRedirectLocation());
            } catch (RequiresHttpAction ignored) {
                return "redirect:/";
            }
        }
    }
}
