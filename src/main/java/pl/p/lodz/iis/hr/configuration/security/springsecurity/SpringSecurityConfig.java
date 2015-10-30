package pl.p.lodz.iis.hr.configuration.security.springsecurity;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    @SuppressWarnings("ProhibitedExceptionDeclared")
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll();
    }
}
