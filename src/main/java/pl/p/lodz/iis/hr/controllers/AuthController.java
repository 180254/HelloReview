package pl.p.lodz.iis.hr.controllers;

import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.oauth.client.GitHubClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.p.lodz.iis.hr.configuration.GHPac4jSecurityHelper;
import pl.p.lodz.iis.hr.utils.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for actions related to authorizations.<br/>
 * <br/>
 * /login    - pl.p.lodz.iis.hr.controllers.AuthController<br/>
 * /logout   - org.pac4j.springframework.web.ApplicationLogoutController<br/>
 * /callback - org.pac4j.springframework.web.CallbackController<br/>
 */
@Controller
class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final GitHubClient gitHubClient;

    @Autowired
    AuthController(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    @RequestMapping(
            value = "/login",
            method = RequestMethod.POST)
    public String login(HttpServletRequest request,
                        HttpServletResponse response) {

        GHPac4jSecurityHelper ghSecurityHelper = new GHPac4jSecurityHelper(gitHubClient, request, response);

        if (ghSecurityHelper.getProfileManager().isAuthenticated()
                && !ExceptionUtils.isExceptionThrown1(ghSecurityHelper::getUserProfileUp2Date)) {
            return "redirect:/";

        } else {
            ghSecurityHelper.getProfileManager().remove(true);

            try {
                return String.format("redirect:%s", ghSecurityHelper.getRedirectLocation());
            } catch (RequiresHttpAction ignored) {
                return "redirect:/";
            }
        }
    }
}
