package pl.p.lodz.iis.hr.models.courses;

public enum CommissionStatus {

    NOT_FORKED(0,
            "m.commission.status.not.forked",
            "comm-status-not-forked"),
    PROCESSING_FAILED(1,
            "m.commission.status.processing.failed",
            "comm-status-processing-failed"),

    PROCESSING(10,
            "m.commission.status.processing",
            "comm-status-processing"),

    UNFILLED(20,
            "m.commission.status.unfilled",
            "comm-status-unfilled"),
    FILLED(21,
            "m.commission.status.filled",
            "comm-status-filled");

    private final int code;
    private final String localeCode;
    private final String cssClass;

    CommissionStatus(int code, String localeCode, String cssClass) {
        this.code = code;
        this.localeCode = localeCode;
        this.cssClass = cssClass;
    }

    public boolean isCopyExistOnGitHub() {
        return code >= UNFILLED.code;
    }

    public boolean shouldBeRetried() {
        return this == PROCESSING_FAILED;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public String getCssClass() {
        return cssClass;
    }
}
