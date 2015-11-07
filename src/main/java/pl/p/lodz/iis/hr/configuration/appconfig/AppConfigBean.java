package pl.p.lodz.iis.hr.configuration.appconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.p.lodz.iis.hr.services.XmlMapperProvider;

@Configuration
class AppConfigBean {

    @Autowired private XmlMapperProvider xmlMapperProvider;

    @Bean
    public AppConfig appConfig() {
        return new AppConfigParser(xmlMapperProvider).getAppConfig();
    }
}
