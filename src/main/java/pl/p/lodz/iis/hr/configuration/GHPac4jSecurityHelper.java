package pl.p.lodz.iis.hr.configuration;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.client.GitHubClient;
import org.pac4j.oauth.profile.github.GitHubProfile;
import pl.p.lodz.iis.hr.appconfig.AppConfig;
import pl.p.lodz.iis.hr.utils.MemoizeSupplier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Supplier;

/**
 * Pac4j security helper class. Provide profile od current logged in used, and operations related to it.
 */
public class GHPac4jSecurityHelper {

    private final GitHubClient gitHubClient;

    private final Supplier<WebContext> webContext;
    private final Supplier<ProfileManager<UserProfile>> profileManager;
    private final Supplier<GitHubProfile> sessionUserProfile;

    public GHPac4jSecurityHelper(GitHubClient gitHubClient,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        this.gitHubClient = gitHubClient;

        webContext = MemoizeSupplier.of(() -> new J2EContext(request, response));
        profileManager = MemoizeSupplier.of(() -> new ProfileManager<>(webContext.get()));
        sessionUserProfile = MemoizeSupplier.of(() -> ((GitHubProfile) profileManager.get().get(true)));
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
        return gitHubClient.getUserProfile(webContext.get(), accessToken);
    }

    public boolean isMaster(AppConfig appConfig) {
        return appConfig.getGitHubConfig().getMasters().getUserNames().contains(
                sessionUserProfile.get().getUsername()
        );
    }

    public boolean isPeer(AppConfig appConfig) {
        return !isMaster(appConfig);
    }
}
