package pl.p.lodz.iis.hr.utils;

import org.eclipse.jdt.annotation.Nullable;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public final class DTFormatter {

    private DTFormatter() {
    }

    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String format(@Nullable TemporalAccessor temporal) {
        return DT_FORMATTER.format(temporal);
    }
}
