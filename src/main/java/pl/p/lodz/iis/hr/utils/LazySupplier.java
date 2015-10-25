package pl.p.lodz.iis.hr.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Lazy loading Supplier on Java8
 * Source: https://gist.github.com/taichi/6daf50919ff276aae74f
 *
 * @param <T> type
 * @author https://gist.github.com/taichi
 */
public class LazySupplier<T> implements Supplier<T> {

    private final Supplier<T> delegate;
    private final Map<Class<?>, T> map = new ConcurrentHashMap<>(1);

    public LazySupplier(Supplier<T> delegate) {
        this.delegate = delegate;
    }

    public static <T> Supplier<T> of(Supplier<T> provider) {
        return new LazySupplier<>(provider);
    }

    @Override
    public T get() {
        return map.computeIfAbsent(LazySupplier.class, key -> delegate.get());
    }
}