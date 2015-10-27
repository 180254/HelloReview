package pl.p.lodz.iis.hr.utils;

import java.util.function.Supplier;

public final class ExceptionChecker {

    private ExceptionChecker() {
    }

    public static <T> boolean checkExceptionThrown(Supplier<T> supplier) {
        try {
            supplier.get();
            return false;
        } catch (Exception ignored) {
            return true;
        }
    }

    public static <T> boolean checkNoExceptionThrown(Supplier<T> supplier) {
        return !checkExceptionThrown(supplier);
    }
}
