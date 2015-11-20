package pl.p.lodz.iis.hr.exceptions;


public class OtherRestProcessingException extends Exception {

    private static final long serialVersionUID = -2579154103815528334L;

    private final String localeCode;
    private final Object[] args;

    public OtherRestProcessingException(String localeCode) {
        this.localeCode = localeCode;
        this.args = null;
    }

    public OtherRestProcessingException(String localeCode, Object[] args) {
        this.localeCode = localeCode;
        this.args = args.clone();
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public Object[] getArgs() {
        return args.clone();
    }
}
