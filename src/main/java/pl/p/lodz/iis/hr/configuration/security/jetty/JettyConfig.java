package pl.p.lodz.iis.hr.configuration.security.jetty;

import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class is to reconfigure Jetty, to do not send server version in header.
 * source: http://stackoverflow.com/questions/15652902/remove-the-http-server-header-in-jetty-9
 */
@Configuration
class JettyConfig {

    @Bean
    public EmbeddedServletContainerCustomizer customizer() {
        return new MyEmbeddedServletContainerCustomizer();
    }
}
