package pl.p.lodz.iis.hr.utils;

import java.util.function.Supplier;

public final class ExceptionChecker {

    private ExceptionChecker() {
    }

    public static <T> boolean checkNoExceptionThrown(Supplier<T> supplier) {
        try {
            supplier.get();
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
