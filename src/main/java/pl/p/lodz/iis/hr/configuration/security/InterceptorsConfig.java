package pl.p.lodz.iis.hr.configuration.security;

import org.pac4j.oauth.client.GitHubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import pl.p.lodz.iis.hr.configuration.security.interceptors.HasRoleMasterInterceptor;
import pl.p.lodz.iis.hr.configuration.security.interceptors.HasRolePeerInterceptor;
import pl.p.lodz.iis.hr.configuration.security.interceptors.UserInfoAppenderInterceptor;
import pl.p.lodz.iis.hr.xmlconfig.XMLConfig;

@Configuration
@ComponentScan(basePackages = "org.pac4j.springframework.web")
@DependsOn({"pack4jConfig", "xmlConfig"})
public class InterceptorsConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private GitHubClient gitHubClient;

    @Autowired
    private XMLConfig xmlConfig;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HasRoleMasterInterceptor(gitHubClient, xmlConfig)).addPathPatterns("/master/**");
        registry.addInterceptor(new HasRolePeerInterceptor(gitHubClient, xmlConfig)).addPathPatterns("/peer/**");
        registry.addInterceptor(new UserInfoAppenderInterceptor(gitHubClient, xmlConfig)).addPathPatterns("/**");
    }
}