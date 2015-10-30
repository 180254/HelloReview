package pl.p.lodz.iis.hr.configuration.appconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfigBean {

    @Bean
    public AppConfig appConfig() {
        return new AppConfigProvider().getAppConfig();
    }
}
