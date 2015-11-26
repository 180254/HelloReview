package pl.p.lodz.iis.hr.controllers;

import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Controller to customize handling error pages.<br/>
 * <br/>
 * If request was non-ajax keep default action.<br/>
 * - error.html template will be rendered.<br/>
 * <br/>
 * If request was ajax:<br/>
 * - Full information about error is logged in by logger.<br/>
 * - Only status code (404, 500, etc) and code message (Not Found, Internal Server Errror, etc) are sent to client.<br/>
 * - If error was coursed by missing or expired CSRF token, code message is replaced to "Invalid CSRF Token".<br/>
 */
@Controller
class CustomizedErrorController extends BasicErrorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomizedErrorController.class);

    CustomizedErrorController() {
        super(new DefaultErrorAttributes(), new ErrorProperties());
    }

    @Override
    public ModelAndView errorHtml(HttpServletRequest request) {
        return super.errorHtml(request);
    }

    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        ResponseEntity<Map<String, Object>> error = super.error(request);

        LOGGER.warn("JSON ERROR {}", error);

        Map<String, Object> body = new HashedMap<>(2);
        body.put("status", error.getBody().get("status"));
        body.put("error", error.getBody().get("error"));

        Object message = error.getBody().get("message");
        if (message instanceof String) {
            if (((String) message).contains("CSRF")) {
                body.put("error", "Invalid CSRF Token");
            }
        }

        return new ResponseEntity<>(body, error.getStatusCode());
    }
}
