package pl.p.lodz.iis.hr.configuration.security.springsecurity;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
/* order caused by https://github.com/spring-projects/spring-boot/issues/3734 */
@Order(Ordered.LOWEST_PRECEDENCE - 10)
class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    @SuppressWarnings("ProhibitedExceptionDeclared")
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll();
    }
}
