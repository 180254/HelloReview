package pl.p.lodz.iis.hr.configuration;


import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;

import java.io.File;

/**
 * Reconfigure Jetty, to log all incoming HTTP request.<br/>
 * http://stackoverflow.com/questions/20574972/spring-boot-jetty-tomcat-embedded-access-log-configuration<br/>
 * http://www.scriptscoop.net/t/88dc1e4792a0/spring-boot-log-all-incoming-http-requests-when-using-an-embedded-jett.html
 *
 * @author dimi
 */
class JettyRequestsLogCustomizer implements JettyServerCustomizer {

    private final JettyRequestLogProperties jettyReqLogProp;

    JettyRequestsLogCustomizer(JettyRequestLogProperties jettyReqLogProp) {
        this.jettyReqLogProp = jettyReqLogProp;
    }

    @Override
    public void customize(Server server) {
        HandlerCollection handlers = new HandlerCollection();
        for (Handler handler : server.getHandlers()) {
            handlers.addHandler(handler);
        }

        NCSARequestLog reqLogImpl = new NCSARequestLog();
        reqLogImpl.setFilename(new File(jettyReqLogProp.getFilename()).getAbsolutePath());
        reqLogImpl.setFilenameDateFormat(jettyReqLogProp.getFilenameDateFormat());
        reqLogImpl.setExtended(jettyReqLogProp.isExtended());
        reqLogImpl.setAppend(jettyReqLogProp.isAppend());
        reqLogImpl.setLogCookies(jettyReqLogProp.isLogCookies());
        reqLogImpl.setLogLatency(jettyReqLogProp.isLogLatency());
        reqLogImpl.setLogServer(jettyReqLogProp.isLogServer());
        reqLogImpl.setIgnorePaths(jettyReqLogProp.getIgnorePathsAsArray());
        reqLogImpl.setLogLocale(jettyReqLogProp.getLocaleObject());
        reqLogImpl.setLogTimeZone(jettyReqLogProp.getLogTimeZone());
        reqLogImpl.setRetainDays(jettyReqLogProp.getRetainDays());

        RequestLogHandler reqLogs = new RequestLogHandler();
        reqLogs.setRequestLog(reqLogImpl);

        handlers.addHandler(reqLogs);
        server.setHandler(handlers);
    }
}

