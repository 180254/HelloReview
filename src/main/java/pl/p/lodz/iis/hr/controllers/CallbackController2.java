package pl.p.lodz.iis.hr.controllers;

import org.pac4j.springframework.web.CallbackController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
class CallbackController2 extends CallbackController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackController2.class);

    @Override
    public String callback(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.debug("IP {}", request.getRemoteAddr());
        return super.callback(request, response);
    }
}
