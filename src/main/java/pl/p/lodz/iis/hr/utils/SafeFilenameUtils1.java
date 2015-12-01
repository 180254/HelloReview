package pl.p.lodz.iis.hr.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public final class SafeFilenameUtils {

    private static final Pattern NOT_SAFE_CHARS = Pattern.compile("[^a-zA-Z0-9.-]");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");

    private SafeFilenameUtils() {
    }

    public static String getCurrentTimestamp() {
        return formatTimestamp(LocalDateTime.now());
    }

    public static String formatTimestamp(ChronoLocalDateTime<LocalDate> localDateTime) {
        return localDateTime.format(DATE_TIME_FORMATTER);
    }

    public static String toFilenameSafeString(CharSequence unsafeString) {
        return NOT_SAFE_CHARS.matcher(unsafeString).replaceAll("_");
    }
}
