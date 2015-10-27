package pl.p.lodz.iis.hr.configuration.security;

import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.oauth.client.GitHubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.p.lodz.iis.hr.xmlconfig.XMLConfig;
import pl.p.lodz.iis.hr.xmlconfig.github.Application;
import pl.p.lodz.iis.hr.xmlconfig.github.General;

@Configuration
public class Pac4jSecurityConfig {

    @Autowired
    private XMLConfig xmlConfig;

    @Bean(name = "pack4jClients")
    public Clients clients() {
        General general = xmlConfig.getGeneral();
        Application application = xmlConfig.getGitHub().getApplication();

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
