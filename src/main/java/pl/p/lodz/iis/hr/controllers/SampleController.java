package pl.p.lodz.iis.hr.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.p.lodz.iis.hr.components.XMLConfigProvider;

@Controller
public class SampleController {

    @Autowired
    XMLConfigProvider xmlConfigProvider;

    @Autowired
    OAuth2RestTemplate oAuth2RestTemplate;

    @RequestMapping("/")
    public String main() {

        Logger logger = LoggerFactory.getLogger(SampleController.class);
        logger.info(oAuth2RestTemplate.getResource().getAccessTokenUri());
        logger.info(oAuth2RestTemplate.getResource().getClientId());

        logger.info(oAuth2RestTemplate.getResource().getClientSecret());
        logger.info(oAuth2RestTemplate.getResource().getGrantType());
        logger.info(oAuth2RestTemplate.getResource().getId());
        logger.info(oAuth2RestTemplate.getResource().getTokenName());

        logger.info(oAuth2RestTemplate.getResource().getAuthenticationScheme().toString());
        logger.info(oAuth2RestTemplate.getResource().getClientAuthenticationScheme().toString());


        return "main";
    }

    @RequestMapping("/auth/callback")
    public String login() {

        return "main";
    }
}


