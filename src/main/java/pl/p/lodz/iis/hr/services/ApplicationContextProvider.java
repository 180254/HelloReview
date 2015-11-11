package pl.p.lodz.iis.hr.services;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationContextProvider.CONTEXT = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        return ApplicationContextProvider.CONTEXT.getBean(clazz);
    }

    public static <T> T getBean(String qualifier, Class<T> clazz) {
        return ApplicationContextProvider.CONTEXT.getBean(qualifier, clazz);
    }
}
