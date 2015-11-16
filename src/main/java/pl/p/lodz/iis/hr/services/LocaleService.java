package pl.p.lodz.iis.hr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.utils.ExceptionUtil;

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
        String msg = ExceptionUtil.ignoreException1(msgSupplier::get);
        return (msg == null)
                ? String.format("??%s%s??", code, getLocale().toString())
                : msg;
    }

    public String get(String code) {
        return msgOrCode(() -> messageSource.getMessage(code, null, getLocale()), code);
    }

    public String get(String code, Object[] args, String defaultMessage) {
        return msgOrCode(() -> messageSource.getMessage(code, args, defaultMessage, getLocale()), code);
    }

    public String get(String code, Object[] args) {
        return msgOrCode(() -> messageSource.getMessage(code, args, getLocale()), code);
    }

    public String get(MessageSourceResolvable resolvable) {
        return msgOrCode(() -> messageSource.getMessage(resolvable, getLocale()), resolvable.getCodes()[0]);
    }
}
