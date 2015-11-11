package pl.p.lodz.iis.hr.configuration.hibernate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;

@Configuration
class Hibernate5Configuration {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.jpa")
    public JpaProperties jpaProperties() {
        Map<String, String> hibernateConfig = new HashMap<>(3);

        hibernateConfig.put("hibernate.ejb.naming_strategy_delegator",
                StringUtils.EMPTY);
        hibernateConfig.put("hibernate.implicit_naming_strategy",
                "org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyHbmImpl");
        hibernateConfig.put("hibernate.physical_naming_strategy",
                "pl.p.lodz.iis.hr.configuration.hibernate.ImprovedNamingStrategy");

        JpaProperties jpaProperties = new JpaProperties();
        jpaProperties.setProperties(hibernateConfig);

        return jpaProperties;
    }
}
