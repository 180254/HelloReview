package pl.p.lodz.iis.hr.configuration;

import org.pac4j.oauth.client.GitHubClient;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pl.p.lodz.iis.hr.appconfig.AppConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class InterceptorHasRoleMaster extends HandlerInterceptorAdapter {

    private final GitHubClient gitHubClient;
    private final AppConfig appConfig;

    InterceptorHasRoleMaster(GitHubClient gitHubClient, AppConfig appConfig) {
        this.gitHubClient = gitHubClient;
        this.appConfig = appConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler)
            throws IOException {

        GHPac4jSecurityHelper ghSecurityHelper = new GHPac4jSecurityHelper(gitHubClient, request, response);

        if (ghSecurityHelper.isAuthenticated()) {
            if (ghSecurityHelper.isMaster(appConfig)) {
                return true;
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

        } else {
            response.sendRedirect("/");
            return false;
        }
    }
}
