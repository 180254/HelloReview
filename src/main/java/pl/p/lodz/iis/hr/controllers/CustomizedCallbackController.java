package pl.p.lodz.iis.hr.controllers;

import org.pac4j.core.exception.TechnicalException;
import org.pac4j.springframework.web.CallbackController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(basePackageClasses = CallbackController.class)
class CustomizedCallbackController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TechnicalException.class)
    public String handleControllerException() {
        return "redirect:/logout?url=/github-issue";
    }

}
