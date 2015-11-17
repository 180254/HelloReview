package pl.p.lodz.iis.hr.configuration;

import org.pac4j.oauth.client.GitHubClient;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pl.p.lodz.iis.hr.appconfig.AppConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class InterceptorUserInfoAppender extends HandlerInterceptorAdapter {

    private final GitHubClient gitHubClient;
    private final AppConfig appConfig;

    InterceptorUserInfoAppender(GitHubClient gitHubClient, AppConfig appConfig) {
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

            GHPac4jSecurityHelper GHPac4JSecurityHelper = new GHPac4jSecurityHelper(gitHubClient, request, response);
            ModelMap model = modelAndView.getModelMap();

            if (GHPac4JSecurityHelper.isAuthenticated()) {
                model.addAttribute("isLoggedIn", true);
                model.addAttribute("isMaster", GHPac4JSecurityHelper.isMaster(appConfig));
                model.addAttribute("isPeer", GHPac4JSecurityHelper.isPeer(appConfig));
                model.addAttribute("username", GHPac4JSecurityHelper.getUserProfileFromSession().getUsername());

            } else {
                model.addAttribute("isLoggedIn", false);
                model.addAttribute("isMaster", false);
                model.addAttribute("isPeer", false);
            }
        }
    }
}
