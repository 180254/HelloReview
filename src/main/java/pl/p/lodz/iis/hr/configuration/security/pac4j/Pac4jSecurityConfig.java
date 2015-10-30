package pl.p.lodz.iis.hr.configuration.security.pac4j;

import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.oauth.client.GitHubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.p.lodz.iis.hr.configuration.appconfig.AppConfig;
import pl.p.lodz.iis.hr.configuration.appconfig.github.Application;
import pl.p.lodz.iis.hr.configuration.appconfig.github.General;

@Configuration
class Pac4jSecurityConfig {

    @Autowired private AppConfig appConfig;

    @Bean(name = "pack4jClients")
    public Clients clients() {
        General general = appConfig.getGeneral();
        Application application = appConfig.getGitHub().getApplication();

        GitHubClient git = new GitHubClient(application.getClientID(), application.getClientSecret());
        return new Clients(String.format("%s/callback", general.getUrl()), git);
    }

    @Bean(name = "pack4jConfig")
    public Config config() {
        return new Config(clients());
    }

    @Bean(name = "pack4jGitHubClient")
    public GitHubClient client() {
        return clients().findClient(GitHubClient.class);
    }
}
