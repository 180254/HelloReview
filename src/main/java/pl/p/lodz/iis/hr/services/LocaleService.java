package pl.p.lodz.iis.hr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.utils.ExceptionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * Get properly localized message (i18n property key) from MessageSource easily.
 *
 * @author original concept by anataliocs @ http://stackoverflow.com/questions/28750292/
 * @author 180254
 */
@Service
public class LocaleService {

    private final MessageSource messageSource;

    @Autowired
    LocaleService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    /**
     * @param msgSupplier supplier of localized msg
     * @param code        localization property key
     * @return localized msg or msg code, if no localization is available
     */
    private String msgOrCode(Supplier<String> msgSupplier, String code) {
        String msg = ExceptionUtils.ignoreException1(msgSupplier::get);
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

    public List<String> getAsList(String code) {
        return Collections.singletonList(get(code));
    }

    public List<String> getAsList(String code, Object[] args, String defaultMessage) {
        return Collections.singletonList(get(code, args, defaultMessage));
    }

    public List<String> getAsList(String code, Object[] args) {
        return Collections.singletonList(get(code, args));
    }

    public List<String> getAsList(MessageSourceResolvable resolvable) {
        return Collections.singletonList(get(resolvable));
    }
}
