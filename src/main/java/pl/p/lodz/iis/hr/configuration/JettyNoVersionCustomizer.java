package pl.p.lodz.iis.hr.configuration;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;

/**
 * Reconfigure Jetty, to do not send server version in header.<br/>
 * http://stackoverflow.com/questions/28339724/customizing-httpconfiguration-of-jetty-with-spring-boot<br/>
 * http://stackoverflow.com/questions/15652902/remove-the-http-server-header-in-jetty-9
 *
 * @author Andy Wilkinson(http://stackoverflow.com/users/1384297/andy-wilkinson)
 */
class JettyNoVersionCustomizer implements JettyServerCustomizer {

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
