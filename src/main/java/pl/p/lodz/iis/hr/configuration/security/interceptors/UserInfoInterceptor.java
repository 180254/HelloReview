package pl.p.lodz.iis.hr.configuration.security.interceptors;

import org.pac4j.oauth.client.GitHubClient;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pl.p.lodz.iis.hr.configuration.security.SecurityHelper;
import pl.p.lodz.iis.hr.xmlconfig.XMLConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class UserInfoInterceptor extends HandlerInterceptorAdapter {

    private final GitHubClient gitHubClient;
    private final XMLConfig xmlConfig;

    public UserInfoInterceptor(GitHubClient gitHubClient, XMLConfig xmlConfig) {
        this.gitHubClient = gitHubClient;
        this.xmlConfig = xmlConfig;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {

        if (modelAndView != null) {
            if(modelAndView.hasView() && modelAndView.getViewName().startsWith("redirect")) {
                return;
            }

            ModelMap model = modelAndView.getModelMap();
            SecurityHelper securityHelper = new SecurityHelper(gitHubClient, request, response);

            model.addAttribute("isLoggedIn", securityHelper.isAuthenticated());
            if (securityHelper.isAuthenticated()) {
                model.addAttribute("isMaster", securityHelper.isMaster(xmlConfig));
                model.addAttribute("username", securityHelper.getUserProfileFromSession().getUsername());
            }
        }
    }

}
