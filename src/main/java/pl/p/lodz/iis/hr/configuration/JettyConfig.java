package pl.p.lodz.iis.hr.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to customize how jetty is configured.
 */
@Configuration
@EnableConfigurationProperties(JettyRequestLogProperties.class)
class JettyConfig {

    @Autowired private JettyRequestLogProperties jettyReqLogProp;

    @Bean
    public EmbeddedServletContainerCustomizer customizer() {
        return new JettyContainerCustomizer(jettyReqLogProp);
    }
}
