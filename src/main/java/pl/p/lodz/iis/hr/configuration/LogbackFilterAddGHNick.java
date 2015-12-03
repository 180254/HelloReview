package pl.p.lodz.iis.hr.configuration;

import org.pac4j.oauth.client.GitHubClient;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @see LogbackFiltersConfig
 */
class LogbackFilterAddGHNick implements Filter {

    private static final String USER_KEY = "username";

    private final GitHubClient ghClient;

    LogbackFilterAddGHNick(GitHubClient ghClient) {
        this.ghClient = ghClient;
    }

    @Override
    public void doFilter(
            ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        boolean successfulRegistration = false;

        try {
            successfulRegistration = addUsernameToMDC(request, response);
            chain.doFilter(request, response);

        } finally {
            if (successfulRegistration) {
                MDC.remove(USER_KEY);
            }
        }
    }

    private boolean addUsernameToMDC(ServletRequest request, ServletResponse response) {
        if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {

            HttpServletRequest request1 = (HttpServletRequest) request;
            HttpServletResponse response1 = (HttpServletResponse) response;

            GHPac4jSecurityHelper pac4jSecurityHelper = new GHPac4jSecurityHelper(ghClient, request1, response1);
            if (pac4jSecurityHelper.isAuthenticated()) {
                MDC.put(USER_KEY, pac4jSecurityHelper.getUserProfileFromSession().getUsername());
                return true;
            }
        }

        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
