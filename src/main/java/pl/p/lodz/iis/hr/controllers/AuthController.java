package pl.p.lodz.iis.hr.controllers;

import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.oauth.client.GitHubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.p.lodz.iis.hr.configuration.pac4j.Pac4jSecurityHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static pl.p.lodz.iis.hr.utils.ExceptionChecker.exceptionThrown;

/**
 * /login  - pl.p.lodz.iis.hr.controllers.AuthController
 * /logout - org.pac4j.springframework.web.ApplicationLogoutController
 * /callback - org.pac4j.springframework.web.CallbackController
 */
@Controller
class AuthController {

    @Autowired private GitHubClient gitHubClient;

    @RequestMapping(
            value = "/login",
            method = RequestMethod.POST)
    public String login(HttpServletRequest request,
                        HttpServletResponse response) {

        Pac4jSecurityHelper pac4jSecurityHelper = new Pac4jSecurityHelper(gitHubClient, request, response);

        if (pac4jSecurityHelper.getProfileManager().isAuthenticated()
                && !exceptionThrown(pac4jSecurityHelper::getUserProfileUp2Date)) {
            return "redirect:/";

        } else {
            pac4jSecurityHelper.getProfileManager().remove(true);

            try {
                return String.format("redirect:%s", pac4jSecurityHelper.getRedirectLocation());
            } catch (RequiresHttpAction ignored) {
                return "redirect:/";
            }
        }
    }
}
