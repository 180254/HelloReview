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
@Order(Ordered.LOWEST_PRECEDENCE - 10) /* order caused by https://github.com/spring-projects/spring-boot/issues/3734 */
class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private HeaderWriter getCSPHeader() {
        Map<String, String> cspMap = new ImmutableMap.Builder<String, String>()
                // @formatter:off
                .put("default-scr", "'none'")
                .put("script-src",  "'self' maxcdn.bootstrapcdn.com cdnjs.cloudflare.com")
             // .put("object-src",  "'none'")

             // style-src 'unsafe-inline' is required for xml displaying in pretty format by browser
                .put("style-src",   "'self' 'unsafe-inline' maxcdn.bootstrapcdn.com cdnjs.cloudflare.com")

                .put("img-src",     "'self'")
             // .put("media-src",   "'none'")
             // .put("frame-src",   "'none'")
                .put("font-src",    "'self' maxcdn.bootstrapcdn.com")
                .put("connect-src", "'self'")

                .put("report-uri", "/csp-reports")
                .build();
                // @formatter:on

        String cspString =
                cspMap.entrySet().stream()
                        .map(cspEntry -> String.format("%s %s;", cspEntry.getKey(), cspEntry.getValue()))
                        .reduce((s1, s2) -> String.format("%s %s", s1, s2))
                        .get();

        return new StaticHeadersWriter("Content-Security-Policy", cspString);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*
        Headers auto added by spring boot:
        - cache control
            Cache-Control: no-cache, no-store, max-age=0, must-revalidate
            Pragma: no-cache
            Expires: 0
        - other
            X-Content-Type-Options: nosniff
            X-XSS-Protection: 1; mode=block
            X-Frame-Options: DENY

         Custom added:
         - headers
            Content-Security-Policy
         - other
            X-UA-Compatible as IE=edge in head.html
         */
        http.headers().addHeaderWriter(getCSPHeader());

        http.csrf().ignoringAntMatchers("/csp-reports");
        http.authorizeRequests().anyRequest().permitAll();
    }
}
