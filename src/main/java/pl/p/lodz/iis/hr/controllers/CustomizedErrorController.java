package pl.p.lodz.iis.hr.controllers;

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

    CustomizedErrorController() {
        super(new DefaultErrorAttributes(), new ErrorProperties());
    }

    @Override
    public ModelAndView errorHtml(HttpServletRequest request) {
        return super.errorHtml(request);
    }

    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        return super.error(request);
//        ResponseEntity<Map<String, Object>> error = super.error(request);
//
//        Map<String, Object> body = new HashedMap<>(2);
//        body.put("error", error.getBody().get("error"));
//        body.put("status", error.getBody().get("status"));
//
//        return new ResponseEntity<>(body, error.getStatusCode());

    }
}
