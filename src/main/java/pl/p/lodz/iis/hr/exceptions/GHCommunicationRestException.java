package pl.p.lodz.iis.hr.exceptions;


public class GHCommunicationRestException extends Exception {

    private static final long serialVersionUID = 1733028451844942478L;

    private final String msg;

    public GHCommunicationRestException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

}
