package pl.p.lodz.iis.hr.configuration;

import org.pac4j.oauth.client.GitHubClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pl.p.lodz.iis.hr.appconfig.AppConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class InterceptorHasRolePeer extends HandlerInterceptorAdapter {

    private final GitHubClient gitHubClient;
    private final AppConfig appConfig;

    InterceptorHasRolePeer(GitHubClient gitHubClient, AppConfig appConfig) {
        this.gitHubClient = gitHubClient;
        this.appConfig = appConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler)
            throws IOException {

        GHPac4jSecurityHelper GHPac4JSecurityHelper = new GHPac4jSecurityHelper(gitHubClient, request, response);

        if (GHPac4JSecurityHelper.isAuthenticated()) {
            if (GHPac4JSecurityHelper.isPeer(appConfig)) {
                return true;
            } else {
                response.sendError(HttpStatus.UNAUTHORIZED.value());
                return false;
            }

        } else {
            response.sendRedirect("/");
            return false;
        }
    }
}
