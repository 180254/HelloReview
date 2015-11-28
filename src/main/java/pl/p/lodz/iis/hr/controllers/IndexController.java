package pl.p.lodz.iis.hr.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * Nothing special, index must be displayable.<br/>
 * Also /github-issue is handled here.
 */
@Controller
class IndexController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(
            value = "/github-issue",
            method = RequestMethod.GET)
    public String gitHubIssue() {
        return "github-issue";
    }

    @RequestMapping(
            value = "/csrf-token",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> csrfToken(ServletRequest request) {
        Object csrf = request.getAttribute("_csrf");

        if (!(csrf instanceof CsrfToken)) {
            return Arrays.asList(null, null, null);
        }

        CsrfToken csrfToken = (CsrfToken) csrf;
        return Arrays.asList(
                csrfToken.getToken(),
                csrfToken.getHeaderName(),
                csrfToken.getParameterName()
        );
    }
}
