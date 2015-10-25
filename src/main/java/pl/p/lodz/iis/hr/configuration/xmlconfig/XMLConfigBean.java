package pl.p.lodz.iis.hr.configuration.xmlconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.p.lodz.iis.hr.xmlconfig.XMLConfig;

@Configuration
public class XMLConfigBean {

    @Bean
    public XMLConfig xmlConfig() {
        return new XMLConfigProvider().getXMLConfig();
    }
}
