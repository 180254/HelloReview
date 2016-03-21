package pl.p.lodz.iis.hr.controllers;

import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import pl.p.lodz.iis.hr.services.LocaleService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Controller to customize handling error pages.<br/>
 * <br/>
 * General:<br/>
 * - Full information about error is logged in by logger.<br/>
 * - Only status code (404, 500, etc) and code message (Not Found, Internal Server Errror, etc) are sent to client.<br/>
 * - If error was coursed by missing or expired CSRF token, code message is replaced to "error.csrf" property.<br/>
 * <br/>
 * Non-ajax request:<br/>
 * - error.html template will be rendered (contains status code, and code message)<br/>
 * <br/>
 * Ajax request:<br/>
 * - return ResponseEntity with customized model map (status code, and code message)
 */
@Controller
class CustomizedErrorController extends BasicErrorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomizedErrorController.class);

    private final LocaleService localeService;

    @Autowired
    CustomizedErrorController(LocaleService localeService) {
        super(new DefaultErrorAttributes(), new ErrorProperties());
        this.localeService = localeService;
    }

    @Override
    public ModelAndView errorHtml(HttpServletRequest request,
                                  HttpServletResponse response) {
        ModelAndView errorHtml = super.errorHtml(request, response);
        Map<String, Object> model = errorHtml.getModel();

        LOGGER.debug("HTML ERROR {}", errorHtml);

        return new ModelAndView(errorHtml.getView(), getCustomizedModel(model));
    }

    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        ResponseEntity<Map<String, Object>> error = super.error(request);
        Map<String, Object> model = error.getBody();

        LOGGER.debug("JSON ERROR {}", error);

        return new ResponseEntity<>(getCustomizedModel(model), error.getStatusCode());
    }

    /**
     * Only status code (404, 500, etc) and code message (Not Found, Internal Server Errror, etc) are sent to client.
     *
     * @param model full model
     * @return new customized model
     */
    private Map<String, Object> getCustomizedModel(Map<String, Object> model) {
        Map<String, Object> newModel = new HashedMap<>(2);
        newModel.put("status", model.get("status"));
        newModel.put("error", getCustomizedError(model.get("error"), model.get("message")));

        return newModel;
    }

    /**
     * If error was coursed by missing or expired CSRF token, code message is replaced to "error.csrf" property.
     *
     * @param error   code message, "error" from model
     * @param message additional message, "message" from model
     * @return customized error
     */
    private Object getCustomizedError(Object error, Object message) {

        if (message instanceof String) {
            if (((String) message).contains("CSRF")) {
                return localeService.get("error.csrf");
            }
        }

        return error;
    }
}
