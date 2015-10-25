package pl.p.lodz.iis.hr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PeerController {

    @RequestMapping("/peer")
    public String index() {
        return "peer/index";
    }
}
