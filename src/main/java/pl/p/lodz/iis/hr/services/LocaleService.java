package pl.p.lodz.iis.hr.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

/*
    original concept: anataliocs @ http://stackoverflow.com/questions/28750292/
 */
@Service
public class LocaleService {

    @Autowired private MessageSource messageSource;

    private Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, getLocale());
    }

    public String getMessage(String code, Object[] args, String defaultMessage) {
        return messageSource.getMessage(code, args, defaultMessage, getLocale());
    }

    public String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, getLocale());
    }

    public String getMessage(MessageSourceResolvable resolvable) {
        return messageSource.getMessage(resolvable, getLocale());
    }
}
