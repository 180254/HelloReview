package pl.p.lodz.iis.hr.models.courses;

public enum CommissionStatus {

    NOT_FORKED(0),
    PROCESSING_FAILED(1),

    PROCESSING(10),

    UNFILLED(20),
    FILLED(21);

    private final int code;

    CommissionStatus(int code) {
        this.code = code;
    }

    public boolean isCopyExistOnGitHub() {
        return code >= UNFILLED.code;
    }

}
