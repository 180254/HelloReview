package pl.p.lodz.iis.hr.models.courses;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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

    public boolean shouldBeRetried(boolean retryOnProcessing) {
        boolean shouldBeRetried = this == PROCESSING_FAILED;
        shouldBeRetried |= retryOnProcessing && (this == PROCESSING);
        return shouldBeRetried;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public String getCssClass() {
        return cssClass;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
