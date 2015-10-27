package pl.p.lodz.iis.hr.configuration.security.interceptors;

import org.eclipse.jetty.http.HttpStatus;
import org.pac4j.oauth.client.GitHubClient;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pl.p.lodz.iis.hr.configuration.security.Pac4jSecurityHelper;
import pl.p.lodz.iis.hr.xmlconfig.XMLConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HasRoleMasterInterceptor extends HandlerInterceptorAdapter {

    private final GitHubClient gitHubClient;
    private final XMLConfig xmlConfig;

    public HasRoleMasterInterceptor(GitHubClient gitHubClient, XMLConfig xmlConfig) {
        this.gitHubClient = gitHubClient;
        this.xmlConfig = xmlConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler)
            throws Exception {

        Pac4jSecurityHelper pac4jSecurityHelper = new Pac4jSecurityHelper(gitHubClient, request, response);

        if (pac4jSecurityHelper.isAuthenticated()) {
            if (pac4jSecurityHelper.isMaster(xmlConfig)) {
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
