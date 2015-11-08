package pl.p.lodz.iis.hr.controllers;

import org.pac4j.core.exception.TechnicalException;
import org.pac4j.oauth.client.GitHubClient;
import org.pac4j.springframework.web.CallbackController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.p.lodz.iis.hr.configuration.security.pac4j.Pac4jSecurityHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice(basePackageClasses = CallbackController.class)
class CustomizedCallbackController extends ResponseEntityExceptionHandler {

    @Autowired private GitHubClient gitHubClient;

    @ExceptionHandler(TechnicalException.class)
    public String handleControllerException(HttpServletRequest request,
                                            HttpServletResponse response,
                                            Model model) {

        Pac4jSecurityHelper pac4jSecurityHelper = new Pac4jSecurityHelper(gitHubClient, request, response);
        pac4jSecurityHelper.getProfileManager().remove(true);

        model.addAttribute("isLoggedIn", false);
        return "main-github-issue";
    }

}
