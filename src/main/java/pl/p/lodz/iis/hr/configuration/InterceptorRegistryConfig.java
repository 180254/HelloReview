package pl.p.lodz.iis.hr.configuration;

import org.pac4j.oauth.client.GitHubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import pl.p.lodz.iis.hr.appconfig.AppConfig;

@Configuration
@DependsOn({"pac4jConfig", "appConfig"})
class InterceptorRegistryConfig extends WebMvcConfigurerAdapter {

    @Autowired private GitHubClient gitHubClient;
    @Autowired private AppConfig appConfig;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new InterceptorHasRoleMaster(gitHubClient, appConfig)).addPathPatterns("/m/**");
        registry.addInterceptor(new InterceptorHasRolePeer(gitHubClient, appConfig)).addPathPatterns("/p/**");
        registry.addInterceptor(new InterceptorUserInfoAppender(gitHubClient, appConfig)).addPathPatterns("/**");
    }
}
