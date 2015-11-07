package pl.p.lodz.iis.hr.configuration.security.other;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
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
        /*

         */
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

        String[] cspString = {""};

        cspMap.entrySet().stream()
                .map(cspEntry -> String.format("%s %s", cspEntry.getKey(), cspEntry.getValue()))
                .reduce((s1, s2) -> String.format("%s; %s", s1, s2))
                .orElse(StringUtils.EMPTY);
        cspMap.forEach((key, header) -> cspString[0] = String.format("%s%s %s; ", cspString[0], key, header));
        return new StaticHeadersWriter("Content-Security-Policy", cspString[0]);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().ignoringAntMatchers("/csp-reports");
        http.headers().addHeaderWriter(getCSPHeader());
        http.authorizeRequests().anyRequest().permitAll();
    }
}
