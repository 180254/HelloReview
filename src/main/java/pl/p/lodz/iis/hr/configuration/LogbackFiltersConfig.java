package pl.p.lodz.iis.hr.configuration;

import ch.qos.logback.classic.helpers.MDCInsertingServletFilter;
import org.pac4j.oauth.client.GitHubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Configure filters to provide own attributes for pattern in loback-spring.xml.
 */
@Configuration
@DependsOn("pac4jConfig")
class LogbackFiltersConfig {

    @Autowired
    private GitHubClient gitHubClient;

    /**
     * Filter to known GH nick of requester.
     *
     * @return LogbackFilterAddGHNick
     */
    @Bean
    public LogbackFilterAddGHNick filterAddGHNick() {
        return new LogbackFilterAddGHNick(gitHubClient);
    }

    /**
     * Filter to know the hostname, request uri, user-agent, etc associated with a given HTTP request.<br/>
     * More info: http://logback.qos.ch/manual/mdc.html
     *
     * @return MDCInsertingServletFilter
     */
    @Bean
    public MDCInsertingServletFilter mdcInsertingServletFilter() {
        return new MDCInsertingServletFilter();
    }

}
