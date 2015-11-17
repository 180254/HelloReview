package pl.p.lodz.iis.hr.configuration;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;

/**
 * Fix Hibernate5 support in spring boot.<br/>
 * Issue description: https://github.com/spring-projects/spring-boot/issues/2763
 *
 * @author holgerstolzenberg (https://github.com/holgerstolzenberg)
 * @author snicoll (https://github.com/snicoll)
 * @author 180254 (hide warning by setting naming_strategy_delegator as empty string)
 */
@Configuration
class Hibernate5Config {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.jpa")
    public JpaProperties jpaProperties() {
        Map<String, String> hibernateConfig = new HashMap<>(3);

        hibernateConfig.put("hibernate.ejb.naming_strategy_delegator", "");

        hibernateConfig.put("hibernate.implicit_naming_strategy",
                "org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyHbmImpl");
        hibernateConfig.put("hibernate.physical_naming_strategy",
                "pl.p.lodz.iis.hr.configuration.Hibernate5PhyNamStrategy");

        JpaProperties jpaProperties = new JpaProperties();
        jpaProperties.setProperties(hibernateConfig);

        return jpaProperties;
    }
}
