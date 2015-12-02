package pl.p.lodz.iis.hr.controllers;

import org.pac4j.springframework.web.CallbackController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for action related to authorization - OAuth2 authorization callback from GitHub.<br/>
 * <br/>
 * It is org.pac4j.springframework.web.ApplicationLogoutController with additional logger info:<br/>
 * - extra info about user IP<br/>
 */
@Controller
class AuthCallbackController extends CallbackController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthCallbackController.class);

    @Override
    public String callback(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.debug("IP {}", request.getRemoteAddr());
        LOGGER.debug("HOST {}", request.getRemoteHost());
        LOGGER.debug("X-FOR {}", request.getHeader("X-FORWARDED-FOR"));
        return super.callback(request, response);
    }
}
