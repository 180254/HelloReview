package pl.p.lodz.iis.hr.configuration.security.interceptors;

import org.pac4j.oauth.client.GitHubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import pl.p.lodz.iis.hr.configuration.appconfig.AppConfig;

@Configuration
@ComponentScan(basePackages = "org.pac4j.springframework.web")
@DependsOn({"pac4jConfig", "appConfig"})
class InterceptorRegistryConfig extends WebMvcConfigurerAdapter {

    @Autowired private GitHubClient gitHubClient;
    @Autowired private AppConfig appConfig;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HasRoleMasterInterceptor(gitHubClient, appConfig)).addPathPatterns("/m/**");
        registry.addInterceptor(new HasRolePeerInterceptor(gitHubClient, appConfig)).addPathPatterns("/p/**");
        registry.addInterceptor(new UserInfoAppenderInterceptor(gitHubClient, appConfig)).addPathPatterns("/**");
    }
}
