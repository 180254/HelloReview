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

        LOGGER.error("JSON ERROR {}", error);

        Map<String, Object> body = new HashedMap<>(2);
        body.put("status", error.getBody().get("status"));
        body.put("error", error.getBody().get("error"));

        Object message = error.getBody().get("message");
        if (message instanceof String) {
            if (((String) message).contains("CSRF")) {
                String csrfError = String.format("%s as Invalid CSRF Token", body.get("error").toString());
                body.put("error", csrfError);
            }
        }

        return new ResponseEntity<>(body, error.getStatusCode());
    }
}
