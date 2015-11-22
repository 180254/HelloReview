package pl.p.lodz.iis.hr.controllers;

import org.pac4j.core.exception.TechnicalException;
import org.pac4j.springframework.web.CallbackController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.p.lodz.iis.hr.exceptions.ErrorPageException;
import pl.p.lodz.iis.hr.exceptions.LocalizableErrorRestException;
import pl.p.lodz.iis.hr.exceptions.LocalizedErrorRestException;
import pl.p.lodz.iis.hr.services.LocaleService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@ControllerAdvice(
        basePackages = "pl.p.lodz.iis.hr.controllers",
        basePackageClasses = CallbackController.class
)
class CustomizedExHandlerController extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomizedExHandlerController.class);

    private final LocaleService localeService;

    @Autowired
    CustomizedExHandlerController(LocaleService localeService) {
        this.localeService = localeService;
    }

    @ExceptionHandler(TechnicalException.class)
    public String handleControllerException(TechnicalException ex) {
        LOGGER.warn("TechnicalException: ", ex);
        return "redirect:/logout?url=/github-issue";
    }


    @ExceptionHandler(LocalizedErrorRestException.class)
    @ResponseBody
    public List<String> handleLocalizedErrRestEx(LocalizedErrorRestException ex,
                                                 HttpServletResponse response) {
        LOGGER.warn("LocalizedErrorRestException", ex);
        response.setStatus(ex.getStatusCode());
        return ex.getErrors();
    }


    @ExceptionHandler(LocalizableErrorRestException.class)
    @ResponseBody
    public List<String> handleLocalizableErrRestEx(LocalizableErrorRestException ex,
                                                   HttpServletResponse response) {
        LOGGER.warn("LocalizableErrorRestException", ex);
        response.setStatus(ex.getStatusCode());
        return localeService.getAsList(ex.getLocaleCode(), ex.getLocaleArgs());
    }

    @ExceptionHandler(ErrorPageException.class)
    public void handleErrorPageEx(ErrorPageException ex,
                                  HttpServletResponse response)
            throws IOException {
        response.sendError(ex.getStatusCode());
    }
}
