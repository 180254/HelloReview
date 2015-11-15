package pl.p.lodz.iis.hr.configuration.jetty;

import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classes used by this configuration are to reconfigure Jetty, to do not send server version in header.<br/>
 * http://stackoverflow.com/questions/28339724/customizing-httpconfiguration-of-jetty-with-spring-boot<br/>
 * http://stackoverflow.com/questions/15652902/remove-the-http-server-header-in-jetty-9
 *
 * @author Andy Wilkinson(http://stackoverflow.com/users/1384297/andy-wilkinson)
 */
@Configuration
class JettyConfig {

    @Bean
    public EmbeddedServletContainerCustomizer customizer() {
        return new MyEmbeddedServletContainerCustomizer();
    }
}
