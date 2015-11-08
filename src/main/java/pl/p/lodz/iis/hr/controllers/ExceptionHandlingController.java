package pl.p.lodz.iis.hr.controllers;

import org.scribe.exceptions.OAuthConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Controller
class ExceptionHandlingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlingController.class);

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Data integrity violation")
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    private String conflict() {
        return "CONFLICT";
    }

    @ExceptionHandler(OAuthConnectionException.class)
    @ResponseBody
    public String oAuthConnectionException() {
        return "OAuthConnectionException";
    }

    // Total control - setup a model and return the view name yourself. Or consider
    // subclassing ExceptionHandlerExceptionResolver (see below).
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleError(HttpServletRequest request,
                              @RequestBody String requestBody,
                              Exception exception) {

        LOGGER.error("EXCEPTION", exception);
        LOGGER.error("Request URL: {}", request.getRequestURL());
        LOGGER.error("Method: {}", request.getMethod());
        LOGGER.error("Request body: {}", requestBody);

        return "EXCEPTION";
    }
}
