package pl.p.lodz.iis.hr.configuration.security;

import org.pac4j.oauth.client.GitHubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import pl.p.lodz.iis.hr.configuration.security.interceptors.HasMasterRoleInterceptor;
import pl.p.lodz.iis.hr.configuration.security.interceptors.HasPeerRoleInterceptor;
import pl.p.lodz.iis.hr.configuration.security.interceptors.UserInfoInterceptor;
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
        registry.addInterceptor(new HasMasterRoleInterceptor(gitHubClient, xmlConfig)).addPathPatterns("/master/**");
        registry.addInterceptor(new HasPeerRoleInterceptor(gitHubClient, xmlConfig)).addPathPatterns("/peer/**");
        registry.addInterceptor(new UserInfoInterceptor(gitHubClient, xmlConfig)).addPathPatterns("/**");
    }
}