package pl.p.lodz.iis.hr.configuration;

import com.google.common.collect.ImmutableMap;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.client.GitHubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pl.p.lodz.iis.hr.appconfig.AppConfig;
import pl.p.lodz.iis.hr.appconfig.GHApplication;

import java.util.Map;

/**
 * Configuration of beans that are provide possibility of log in using OAuth by GitHub.
 */
@Configuration
@ComponentScan(basePackages = "org.pac4j.springframework.web")
class GHPac4jSecurityConfig {

    private final Map<String, Integer> httpDefaultPorts = ImmutableMap.of("http", 80, "https", 443);

    @Autowired private AppConfig appConfig;

    @Bean(name = "pac4jClients")
    public Clients clients() {
        GHApplication application = appConfig.getGitHubConfig().getApplication();

        GitHubClient git = new GitHubClient(application.getClientID(), application.getClientSecret());
        git.setCallbackUrlResolver(this::getCallbackUrl);

        return new Clients("/callback", git);
    }

    @Bean(name = "pac4jConfig")
    public Config config() {
        return new Config(clients());
    }

    @Bean(name = "pac4jGitHubClient")
    public GitHubClient client() {
        return clients().findClient(GitHubClient.class);
    }

    private String getCallbackUrl(String callback, WebContext request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        String serverPort2 = (serverPort == httpDefaultPorts.get(scheme)) ? "" : (":" + serverPort);
        return scheme + "://" + serverName + serverPort2 + callback;
    }
}
