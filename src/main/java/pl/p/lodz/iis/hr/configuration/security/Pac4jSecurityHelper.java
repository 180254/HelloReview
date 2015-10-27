package pl.p.lodz.iis.hr.configuration.security;


import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.client.GitHubClient;
import org.pac4j.oauth.profile.github.GitHubProfile;
import pl.p.lodz.iis.hr.utils.LazySupplier;
import pl.p.lodz.iis.hr.xmlconfig.XMLConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Supplier;

public class Pac4jSecurityHelper {

    private final GitHubClient gitHubClient;

    private final Supplier<WebContext> webContext;
    private final Supplier<ProfileManager<UserProfile>> profileManager;
    private final Supplier<GitHubProfile> sessionUserProfile;

    public Pac4jSecurityHelper(GitHubClient gitHubClient,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        this.gitHubClient = gitHubClient;
        webContext = LazySupplier.of(() -> new J2EContext(request, response));
        profileManager = LazySupplier.of(() -> new ProfileManager<>(webContext.get()));
        sessionUserProfile = LazySupplier.of(() -> ((GitHubProfile) profileManager.get().get(true)));
    }

    public boolean isAuthenticated() {
        return profileManager.get().isAuthenticated();
    }

    public WebContext getWebContext() {
        return webContext.get();
    }

    public ProfileManager<UserProfile> getProfileManager() {
        return profileManager.get();
    }

    public String getRedirectLocation() throws RequiresHttpAction {
        return gitHubClient.getRedirectAction(webContext.get(), false).getLocation();
    }

    public GitHubProfile getUserProfileFromSession() {
        return sessionUserProfile.get();
    }

    public GitHubProfile getUserProfileUp2Date() {
        String accessToken = sessionUserProfile.get().getAccessToken();
        return gitHubClient.getUserProfile(accessToken);
    }

    public boolean isMaster(XMLConfig xmlConfig) {
        return sessionUserProfile.get().getUsername()
                .equals(xmlConfig.getGitHub().getMaster().getUsername());
    }

    public boolean isPeer(XMLConfig xmlConfig) {
        return !isMaster(xmlConfig);
    }
}
