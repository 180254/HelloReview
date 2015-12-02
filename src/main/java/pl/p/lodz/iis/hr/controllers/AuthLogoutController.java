package pl.p.lodz.iis.hr.controllers;

import org.pac4j.springframework.web.ApplicationLogoutController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

/**
 * Controller for action related to authorizations - logout from system.<br/>
 * <br/>
 * It is just org.pac4j.springframework.web.ApplicationLogoutController without any changes.
 */
@Controller
class AuthLogoutController extends ApplicationLogoutController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthLogoutController.class);

}
