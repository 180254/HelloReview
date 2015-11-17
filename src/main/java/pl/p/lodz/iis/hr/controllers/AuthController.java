package pl.p.lodz.iis.hr.controllers;

import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.oauth.client.GitHubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.p.lodz.iis.hr.configuration.GHPac4jSecurityHelper;
import pl.p.lodz.iis.hr.utils.ExceptionUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

        GHPac4jSecurityHelper GHPac4JSecurityHelper = new GHPac4jSecurityHelper(gitHubClient, request, response);

        if (GHPac4JSecurityHelper.getProfileManager().isAuthenticated()
                && !ExceptionUtil.isExceptionThrown1(GHPac4JSecurityHelper::getUserProfileUp2Date)) {
            return "redirect:/";

        } else {
            GHPac4JSecurityHelper.getProfileManager().remove(true);

            try {
                return String.format("redirect:%s", GHPac4JSecurityHelper.getRedirectLocation());
            } catch (RequiresHttpAction ignored) {
                return "redirect:/";
            }
        }
    }
}
