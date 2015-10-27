package pl.p.lodz.iis.hr.configuration.security;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * this class is to reconfigure Jetty, to do not send server version in header
 * source: http://stackoverflow.com/questions/15652902/remove-the-http-server-header-in-jetty-9
 */
@Configuration
public class JettyConfig {

    @Bean
    public EmbeddedServletContainerCustomizer customizer() {
        return new MyEmbeddedServletContainerCustomizer();

    }

    private static class MyEmbeddedServletContainerCustomizer implements EmbeddedServletContainerCustomizer {

        @Override
        public void customize(ConfigurableEmbeddedServletContainer container) {
            if (container instanceof JettyEmbeddedServletContainerFactory) {
                customizeJetty((JettyEmbeddedServletContainerFactory) container);
            }
        }

        private void customizeJetty(JettyEmbeddedServletContainerFactory jetty) {
            jetty.addServerCustomizers(new MyJettyServerCustomizer());
        }
    }

    private static class MyJettyServerCustomizer implements JettyServerCustomizer {

        @Override
        public void customize(Server server) {
            for (Connector connector : server.getConnectors()) {
                if (connector instanceof ServerConnector) {
                    HttpConnectionFactory connectionFactory = connector.getConnectionFactory(HttpConnectionFactory.class);
                    connectionFactory.getHttpConfiguration().setSendServerVersion(false);
                }
            }
        }
    }
}
