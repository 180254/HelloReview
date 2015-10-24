package pl.p.lodz.iis.hr.controllers;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import pl.p.lodz.iis.hr.components.XMLConfigProvider;

@Controller
public class SampleController {

    @Autowired XMLConfigProvider xmlConfigProvider;

    @RequestMapping("/")
    public String main() {

        LoggerFactory.getLogger(SampleController.class).error("X");
        System.out.println(xmlConfigProvider.getXMLConfig().toString());
        return "main";
    }
}
