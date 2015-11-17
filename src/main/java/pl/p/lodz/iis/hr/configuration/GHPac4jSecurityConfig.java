package pl.p.lodz.iis.hr.configuration;

import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.oauth.client.GitHubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pl.p.lodz.iis.hr.appconfig.AppConfig;
import pl.p.lodz.iis.hr.appconfig.GHApplication;
import pl.p.lodz.iis.hr.appconfig.GeneralConfig;

@Configuration
@ComponentScan(basePackages = "org.pac4j.springframework.web")
class GHPac4jSecurityConfig {

    @Autowired private AppConfig appConfig;

    @Bean(name = "pac4jClients")
    public Clients clients() {
        GeneralConfig generalConfig = appConfig.getGeneralConfig();
        GHApplication application = appConfig.getGitHubConfig().getApplication();

        GitHubClient git = new GitHubClient(application.getClientID(), application.getClientSecret());
        return new Clients(String.format("%s/callback", generalConfig.getUrl()), git);
    }

    @Bean(name = "pac4jConfig")
    public Config config() {
        return new Config(clients());
    }

    @Bean(name = "pac4jGitHubClient")
    public GitHubClient client() {
        return clients().findClient(GitHubClient.class);
    }
}
