package pl.p.lodz.iis.hr.configuration.security.interceptors;

import org.pac4j.oauth.client.GitHubClient;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pl.p.lodz.iis.hr.configuration.appconfig.AppConfig;
import pl.p.lodz.iis.hr.configuration.security.pac4j.Pac4jSecurityHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class UserInfoAppenderInterceptor extends HandlerInterceptorAdapter {

    private final GitHubClient gitHubClient;
    private final AppConfig appConfig;

    UserInfoAppenderInterceptor(GitHubClient gitHubClient, AppConfig appConfig) {
        this.gitHubClient = gitHubClient;
        this.appConfig = appConfig;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) {

        if (modelAndView != null) {
            if (modelAndView.hasView() && modelAndView.getViewName().startsWith("redirect")) {
                return;
            }

            ModelMap model = modelAndView.getModelMap();
            Pac4jSecurityHelper pac4jSecurityHelper = new Pac4jSecurityHelper(gitHubClient, request, response);

            model.addAttribute("isLoggedIn", pac4jSecurityHelper.isAuthenticated());
            if (pac4jSecurityHelper.isAuthenticated()) {
                model.addAttribute("isMaster", pac4jSecurityHelper.isMaster(appConfig));
                model.addAttribute("username", pac4jSecurityHelper.getUserProfileFromSession().getUsername());
            }
        }
    }

}
