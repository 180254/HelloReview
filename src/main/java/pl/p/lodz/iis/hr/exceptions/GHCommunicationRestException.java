package pl.p.lodz.iis.hr.exceptions;


public class GHCommunicationRestException extends Exception {

    private static final long serialVersionUID = 1733028451844942478L;

    private final String errorMsg;

    public GHCommunicationRestException(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
