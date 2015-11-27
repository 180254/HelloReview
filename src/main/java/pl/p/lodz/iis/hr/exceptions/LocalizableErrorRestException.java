package pl.p.lodz.iis.hr.exceptions;


import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

public class LocalizableErrorRestException extends Exception {

    private static final long serialVersionUID = -2579154103815528334L;

    private final int statusCode;
    private final String localeCode;
    private final Object[] localeArgs;


    public LocalizableErrorRestException(int statusCode, String localeCode, String... localeArgs) {
        super(String.join(",", Arrays.<String>asList(String.valueOf(statusCode), localeCode, ezFormat(localeArgs))));

        this.statusCode = statusCode;
        this.localeCode = localeCode;
        this.localeArgs = localeArgs.clone();
    }

    public LocalizableErrorRestException(String localeCode, String... localeArgs) {
        super(String.join(",", Arrays.asList(localeCode, ezFormat(localeArgs))));

        this.statusCode = HttpServletResponse.SC_BAD_REQUEST;
        this.localeCode = localeCode;
        this.localeArgs = localeArgs.clone();
    }

    public static LocalizableErrorRestException noResource() {
        return new LocalizableErrorRestException(HttpServletResponse.SC_NOT_FOUND, "NoResource");
    }

    public static LocalizableErrorRestException noResources() {
        return new LocalizableErrorRestException(HttpServletResponse.SC_NOT_FOUND, "NoResources");
    }

    public static LocalizableErrorRestException badResource() {
        return new LocalizableErrorRestException("BadResource");
    }

    public static LocalizableErrorRestException notUniqueName() {
        return new LocalizableErrorRestException("UniqueName");
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public Object[] getLocaleArgs() {
        return localeArgs.clone();
    }

    public static String ezFormat(Object[] args) {
        String format = new String(new char[args.length]).replace("\0", "[ %s ]");
        return String.format(format, args);
    }
}
