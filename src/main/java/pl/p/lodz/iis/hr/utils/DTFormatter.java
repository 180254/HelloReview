package pl.p.lodz.iis.hr.utils;

import org.jetbrains.annotations.Nullable;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public final class DTFormatter {

    private DTFormatter() {
    }

    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String format(@Nullable TemporalAccessor temporal) {
        if (temporal == null) {
            return "";
        }

        return DT_FORMATTER.format(temporal);
    }
}
