package pl.p.lodz.iis.hr.configuration.security.other;

import com.google.common.collect.ImmutableMap;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

import java.util.Map;

@Configuration
/* order caused by https://github.com/spring-projects/spring-boot/issues/3734 */
@Order(Ordered.LOWEST_PRECEDENCE - 10)
class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private HeaderWriter getCSPHeader() {
        Map<String, String> cspMap = new ImmutableMap.Builder<String, String>()
                // @formatter:off
                .put("default-scr", "'none'")
                .put("script-src",  "'self' maxcdn.bootstrapcdn.com cdnjs.cloudflare.com")
                // .put("object-src",  "'none'")
                .put("style-src",   "'self' maxcdn.bootstrapcdn.com cdnjs.cloudflare.com")
                .put("img-src",     "'self'")
                // .put("media-src",   "'none'")
                // .put("frame-src",   "'none'")
                .put("font-src",    "'self' maxcdn.bootstrapcdn.com")
                .put("connect-src", "'self'")
                .build();
                // @formatter:on

        String[] cspString = {""};
        cspMap.forEach((key, header) -> cspString[0] = String.format("%s%s %s; ", cspString[0], key, header));
        return new StaticHeadersWriter("Content-Security-Policy", cspString[0]);
    }

    @Override
    @SuppressWarnings("ProhibitedExceptionDeclared")
    protected void configure(HttpSecurity http) throws Exception {
//        http.headers().addHeaderWriter(getCSPHeader());
        http.authorizeRequests().anyRequest().permitAll();
    }
}
