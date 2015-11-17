package pl.p.lodz.iis.hr.configuration;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;

class JettyServerCustomizer2 implements JettyServerCustomizer {

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
