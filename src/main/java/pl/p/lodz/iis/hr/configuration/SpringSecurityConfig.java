package pl.p.lodz.iis.hr.configuration;

import com.google.common.collect.ImmutableMap;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

import java.util.Map;

/**
 * General spring security configuration.<br/>
 * <br/>
 * For now request headers are customized here, to be more secure.<br/>
 */
@Configuration
@Order(Ordered.LOWEST_PRECEDENCE - 10) /* order caused by https://github.com/spring-projects/spring-boot/issues/3734 */
class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private HeaderWriter cspMapToHeader(Map<String, String> cspMap) {
        String cspString =
                cspMap.entrySet().stream()
                        .map(cspEntry -> String.format("%s %s", cspEntry.getKey(), cspEntry.getValue()))
                        .reduce((s1, s2) -> String.format("%s; %s", s1, s2))
                        .get();

        return new StaticHeadersWriter("Content-Security-Policy", cspString);
    }

    private HeaderWriter getCSPHeaderSelf() {
        Map<String, String> cspMap = new ImmutableMap.Builder<String, String>()
                // @formatter:off
                .put("default-src",     "'none'")

                .put("connect-src",     "'self'")
                .put("font-src",        "'self'")
                .put("img-src",         "'self' data:")
             // .put("media-src ",      "'none'") // unnecessary - default-src
             // .put("object-src",      "'none'") // unnecessary - default-src
                .put("script-src",      "'self'")
                .put("style-src",       "'self' 'unsafe-inline'")
             // .put("frame-src",       "") // deprecated in CSP2, SHOULD use the child-src directive instead.
             // .put("sandbox",         "") // unnecessary -  object-src is none
                .put("form-action",     "'self' github.com") // necessary - form-action doesn't fall back to the default
                .put("frame-ancestors", "'none'") // necessary - frame-ancestors doesn't fall back to the default src
             // .put("plugin-types",    "") // unnecessary -  object-src is none
             // .put("base-uri",        "") // just self, don't define base-uri
             // .put("child-src",       "'none'") // unnecessary - default-src

                .put("report-uri ", "/csp-reports")
                .build();
                // @formatter:on

        return cspMapToHeader(cspMap);
    }


    private HeaderWriter getCSPHeader() {
        Map<String, String> cspMap = new ImmutableMap.Builder<String, String>()
                // @formatter:off
                // Note: img-src data: is used by toastr.js
                // Note: img-src cdnjs.cloudflare.com is used by X-editable
                // Note: style-src 'unsafe-inline' is required for xml displaying in pretty format by browser (chrome)
                // Note: form-action github.com is required to redirect user to github in authentication

                .put("default-src",     "'none'")

                .put("connect-src",     "'self'")
                .put("font-src",        "'self' cdnjs.cloudflare.com")
                .put("img-src",         "'self' data: cdnjs.cloudflare.com")
             // .put("media-src ",      "'none'") // unnecessary - default-src
             // .put("object-src",      "'none'") // unnecessary - default-src
                .put("script-src",      "'self' cdnjs.cloudflare.com")
                .put("style-src",       "'self' 'unsafe-inline' cdnjs.cloudflare.com")
             // .put("frame-src",       "") // deprecated in CSP2, SHOULD use the child-src directive instead.
             // .put("sandbox",         "") // unnecessary -  object-src is none
                .put("form-action",     "'self' github.com") // necessary - form-action doesn't fall back to the default
                .put("frame-ancestors", "'none'") // necessary - frame-ancestors doesn't fall back to the default src
             // .put("plugin-types",    "") // unnecessary -  object-src is none
             // .put("base-uri",        "") // just self, don't define base-uri
             // .put("child-src",       "'none'") // unnecessary - default-src

                .put("report-uri ", "/csp-reports")
                .build();
                // @formatter:on

        return cspMapToHeader(cspMap);
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
         - meta
            X-UA-Compatible as IE=edge in head.html
         */
        http.headers().addHeaderWriter(getCSPHeaderSelf());
        http.csrf().ignoringAntMatchers("/csp-reports");

        /*
        Disable default authorization. It is done manually using spring-webmvc-pac4j.
        However, the basic auth is used for security of h2-console.
         */
        http.authorizeRequests().anyRequest().permitAll();
    }
}
