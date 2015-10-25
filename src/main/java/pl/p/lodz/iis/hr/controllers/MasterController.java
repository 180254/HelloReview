package pl.p.lodz.iis.hr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MasterController {

    @RequestMapping("/master")
    public String index() {
        return "master/index";
    }
}
