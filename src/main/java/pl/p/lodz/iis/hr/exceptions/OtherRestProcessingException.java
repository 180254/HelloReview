package pl.p.lodz.iis.hr.exceptions;


import javax.servlet.http.HttpServletResponse;

public class OtherRestProcessingException extends Exception {

    private static final long serialVersionUID = -2579154103815528334L;

    private int statusCode;
    private final String localeCode;
    private final Object[] localeArgs;

    public OtherRestProcessingException(String localeCode) {
        statusCode = HttpServletResponse.SC_BAD_REQUEST;
        this.localeCode = localeCode;
        this.localeArgs = null;
    }

    public OtherRestProcessingException(String localeCode, Object[] localeArgs) {
        this.localeCode = localeCode;
        this.localeArgs = localeArgs.clone();
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public Object[] getLocaleArgs() {
        return localeArgs.clone();
    }

    public OtherRestProcessingException setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }
}
