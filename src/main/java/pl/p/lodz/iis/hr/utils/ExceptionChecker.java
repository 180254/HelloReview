package pl.p.lodz.iis.hr.utils;

import java.util.function.Supplier;

public final class ExceptionChecker {

    private ExceptionChecker() {
    }

    public static <T> boolean exceptionThrown(Supplier<T> supplier) {
        try {
            supplier.get();
            return false;
        } catch (RuntimeException ignored) {
            return true;
        }
    }

    public static <T> boolean exceptionThrown2(SupplierWithException<T> supplier) {
        try {
            supplier.get();
            return false;
        } catch (Exception ignored) {
            return true;
        }
    }

    public static boolean exceptionThrown2(SupplierWithException2 supplier) {
        try {
            supplier.get();
            return false;
        } catch (Exception ignored) {
            return true;
        }
    }

    public static <T> T ignoreException(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    public static <T> T ignoreException2(SupplierWithException<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception ignored) {
            return null;
        }
    }

    public static void ignoreException2(SupplierWithException2 supplier) {
        try {
            supplier.get();
        } catch (Exception ignored) {
        }
    }


}
