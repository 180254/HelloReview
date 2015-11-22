package pl.p.lodz.iis.hr.models.courses;

import com.google.common.base.MoreObjects;
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

    public boolean isUnfilled() {
        return code == UNFILLED.code;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("code", code)
                .add("localeCode", localeCode)
                .add("cssClass", cssClass)
                .toString();
    }
}
