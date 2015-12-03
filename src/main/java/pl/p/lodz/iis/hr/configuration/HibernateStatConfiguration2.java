package pl.p.lodz.iis.hr.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @see HibernateStatConfiguration
 */
@Configuration
@DependsOn("hibernateStatInterceptor")
class HibernateStatConfiguration2 extends WebMvcConfigurerAdapter {

    @Autowired
    private HibernateStatInterceptor hibernateStatInterceptor;

    @Bean
    public HibernateStatInterceptor2 hibernateRequestStatInterceptor() {
        return new HibernateStatInterceptor2(hibernateStatInterceptor);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(hibernateRequestStatInterceptor()).addPathPatterns("/**");
    }
}
