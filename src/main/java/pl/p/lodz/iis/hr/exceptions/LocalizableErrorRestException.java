package pl.p.lodz.iis.hr.exceptions;


import javax.servlet.http.HttpServletResponse;

public class LocalizableErrorRestException extends Exception {

    private static final long serialVersionUID = -2579154103815528334L;

    private final int statusCode;
    private final String localeCode;
    private final Object[] localeArgs;


    public LocalizableErrorRestException(int statusCode, String localeCode, String... localeArgs) {
        this.statusCode = statusCode;
        this.localeCode = localeCode;
        this.localeArgs = localeArgs.clone();
    }

    public LocalizableErrorRestException(String localeCode, String... localeArgs) {
        this.statusCode = HttpServletResponse.SC_BAD_REQUEST;
        this.localeCode = localeCode;
        this.localeArgs = localeArgs.clone();
    }

    public static LocalizableErrorRestException noResource() {
        return new LocalizableErrorRestException("NoResource");
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
}
