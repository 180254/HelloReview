package pl.p.lodz.iis.hr.configuration;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;

/**
 * @see JettyConfig
 */
class JettyContainerCustomizer implements EmbeddedServletContainerCustomizer {

    private final JettyRequestLogProperties jettyReqLogProp;

    JettyContainerCustomizer(JettyRequestLogProperties jettyReqLogProp) {
        this.jettyReqLogProp = jettyReqLogProp;
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        if (container instanceof JettyEmbeddedServletContainerFactory) {
            customizeJetty((JettyEmbeddedServletContainerFactory) container);
        }
    }

    private void customizeJetty(JettyEmbeddedServletContainerFactory jetty) {
        jetty.addServerCustomizers(new JettyNoVersionCustomizer());
        jetty.addServerCustomizers(new JettyRequestsLogCustomizer(jettyReqLogProp));

    }
}
