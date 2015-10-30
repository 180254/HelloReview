package pl.p.lodz.iis.hr.configuration.security.interceptors;

import org.eclipse.jetty.http.HttpStatus;
import org.pac4j.oauth.client.GitHubClient;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pl.p.lodz.iis.hr.configuration.appconfig.AppConfig;
import pl.p.lodz.iis.hr.configuration.security.pac4j.Pac4jSecurityHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class HasRoleMasterInterceptor extends HandlerInterceptorAdapter {

    private final GitHubClient gitHubClient;
    private final AppConfig appConfig;

    HasRoleMasterInterceptor(GitHubClient gitHubClient, AppConfig appConfig) {
        this.gitHubClient = gitHubClient;
        this.appConfig = appConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler)
            throws IOException {

        Pac4jSecurityHelper pac4jSecurityHelper = new Pac4jSecurityHelper(gitHubClient, request, response);

        if (pac4jSecurityHelper.isAuthenticated()) {
            if (pac4jSecurityHelper.isMaster(appConfig)) {
                return true;
            } else {
                response.sendError(HttpStatus.UNAUTHORIZED_401);
                return false;
            }

        } else {
            response.sendRedirect("/");
            return false;
        }
    }
}
