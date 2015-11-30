package pl.p.lodz.iis.hr.configuration;

import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class JettyConfig {

    @Bean
    public EmbeddedServletContainerCustomizer customizer() {
        return new JettyContainerCustomizer();
    }
}
