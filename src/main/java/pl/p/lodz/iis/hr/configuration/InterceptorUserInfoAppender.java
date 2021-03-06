package pl.p.lodz.iis.hr.configuration;

import org.pac4j.oauth.client.GitHubClient;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pl.p.lodz.iis.hr.appconfig.AppConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor to append basic info:<br/>
 * - isLoggedIn<br/>
 * - isMaster<br/>
 * - isPeer<br/>
 * - username<br/>
 * about current user to each request.
 */
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

            GHPac4jSecurityHelper ghSecurityHelper = new GHPac4jSecurityHelper(gitHubClient, request, response);
            ModelMap model = modelAndView.getModelMap();

            if (ghSecurityHelper.isAuthenticated()) {
                model.addAttribute("isLoggedIn", true);
                model.addAttribute("isMaster", ghSecurityHelper.isMaster(appConfig));
                model.addAttribute("isPeer", ghSecurityHelper.isPeer(appConfig));
                model.addAttribute("username", ghSecurityHelper.getUserProfileFromSession().getUsername());

            } else {
                model.addAttribute("isLoggedIn", false);
                model.addAttribute("isMaster", false);
                model.addAttribute("isPeer", false);
            }
        }
    }
}
