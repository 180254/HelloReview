package pl.p.lodz.iis.hr.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class CSPController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSPController.class);

    @RequestMapping(
            value = "/csp-reports",
            method = RequestMethod.POST)
    @ResponseBody
    public void cspReports(@RequestBody String str) {
        LOGGER.error(str);
    }
}
