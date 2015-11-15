package pl.p.lodz.iis.hr.utils;

@FunctionalInterface
public interface SupplierWithException<T> {
    T get() throws Exception;
}
