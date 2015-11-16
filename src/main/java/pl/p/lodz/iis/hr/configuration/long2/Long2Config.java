package pl.p.lodz.iis.hr.configuration.long2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class Long2Config extends WebMvcConfigurerAdapter {

    @Bean
    public Long2Converter long2Converter() {
        return new Long2Converter();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(long2Converter());
    }
}
