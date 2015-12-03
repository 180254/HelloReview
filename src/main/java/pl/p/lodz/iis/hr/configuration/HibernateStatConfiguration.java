package pl.p.lodz.iis.hr.configuration;

import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * This configuration is to "Counting Queries per Request with Hibernate and Spring".<br/>
 * Source: http://knes1.github.io/blog/2015/2015-07-08-counting-queries-per-request-with-hibernate-and-spring.html
 *
 * @author Krešimir Nesek (http://knes1.github.io)
 */
@Configuration
class HibernateStatConfiguration extends HibernateJpaAutoConfiguration {

    @Bean(name = "hibernateStatInterceptor")
    public HibernateStatInterceptor hibernateStatInterceptor() {
        return new HibernateStatInterceptor();
    }

    @Override
    protected void customizeVendorProperties(Map<String, Object> vendorProperties) {
        vendorProperties.put("hibernate.ejb.interceptor", hibernateStatInterceptor());
    }
}
