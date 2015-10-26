package pl.p.lodz.iis.hr.configuration.security.interceptors;

import org.eclipse.jetty.http.HttpStatus;
import org.pac4j.oauth.client.GitHubClient;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pl.p.lodz.iis.hr.configuration.security.SecurityHelper;
import pl.p.lodz.iis.hr.xmlconfig.XMLConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HasPeerRoleInterceptor extends HandlerInterceptorAdapter {

    private final GitHubClient gitHubClient;
    private final XMLConfig xmlConfig;

    public HasPeerRoleInterceptor(GitHubClient gitHubClient, XMLConfig xmlConfig) {
        this.gitHubClient = gitHubClient;
        this.xmlConfig = xmlConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler)
            throws Exception {

        SecurityHelper securityHelper = new SecurityHelper(gitHubClient, request, response);

        if (securityHelper.isAuthenticated()) {
            if (securityHelper.isPeer(xmlConfig)) {
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
