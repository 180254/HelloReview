package pl.p.lodz.iis.hr.controllers;

import org.pac4j.core.exception.TechnicalException;
import org.pac4j.springframework.web.CallbackController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.p.lodz.iis.hr.exceptions.*;
import pl.p.lodz.iis.hr.services.LocaleService;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@ControllerAdvice(
        basePackages = "pl.p.lodz.iis.hr.controllers",
        basePackageClasses = CallbackController.class
)
class CustomizedExHandlerController extends ResponseEntityExceptionHandler {

    private final LocaleService localeService;

    @Autowired
    CustomizedExHandlerController(LocaleService localeService) {
        this.localeService = localeService;
    }

    @ExceptionHandler(TechnicalException.class)
    public String handleControllerException() {
        return "redirect:/logout?url=/github-issue";
    }

    @ExceptionHandler(ResourceNotFoundRestException.class)
    @ResponseBody
    public List<String> handleResNotFoundRestEx(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return localeService.getAsList("NoResource");
    }

    @ExceptionHandler(NotUniqueNameException.class)
    @ResponseBody
    public List<String> notUniqueNameException(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return localeService.getAsList("UniqueName");
    }

    @ExceptionHandler(FieldValidationRestException.class)
    @ResponseBody
    public List<String> handleFieldValidRestEx(FieldValidationRestException ex,
                                               HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return ex.getErrors();
    }

    @ExceptionHandler(GHCommunicationRestException.class)
    @ResponseBody
    public List<String> ghCommRestExc(GHCommunicationRestException ex,
                                      HttpServletResponse response) {

        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        return Collections.singletonList(ex.toString());
    }

    @ExceptionHandler(OtherRestProcessingException.class)
    @ResponseBody
    public List<String> handleOtherRestProcEx(OtherRestProcessingException ex,
                                              HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return localeService.getAsList(ex.getLocaleCode(), ex.getArgs());
    }
}
