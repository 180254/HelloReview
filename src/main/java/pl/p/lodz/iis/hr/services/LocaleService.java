package pl.p.lodz.iis.hr.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.utils.ExceptionChecker;

import java.util.Locale;
import java.util.function.Supplier;

/*
    original concept: anataliocs @ http://stackoverflow.com/questions/28750292/
 */
@Service
public class LocaleService {

    @Autowired private MessageSource messageSource;

    private Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    private String msgOrCode(Supplier<String> msgSupplier, String code) {
        String msg = ExceptionChecker.getOrNullIfException(msgSupplier);
        return (msg == null)
                ? String.format("??%s%s??", code, getLocale().toString())
                : msg;
    }

    public String getMessage(String code) {
        return msgOrCode(() -> messageSource.getMessage(code, null, getLocale()), code);
    }

    public String getMessage(String code, Object[] args, String defaultMessage) {
        return msgOrCode(() -> messageSource.getMessage(code, args, defaultMessage, getLocale()), code);
    }

    public String getMessage(String code, Object[] args) {
        return msgOrCode(() -> messageSource.getMessage(code, args, getLocale()), code);
    }

    public String getMessage(MessageSourceResolvable resolvable) {
        return msgOrCode(() -> messageSource.getMessage(resolvable, getLocale()), resolvable.getCodes()[0]);
    }
}
