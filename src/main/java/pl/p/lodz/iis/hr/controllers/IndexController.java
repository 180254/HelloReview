package pl.p.lodz.iis.hr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class IndexController {


    @RequestMapping("/")
    public String index(HttpServletRequest request,
                        HttpServletResponse response,
                        Model model) {

        return "main-index";
    }
}
