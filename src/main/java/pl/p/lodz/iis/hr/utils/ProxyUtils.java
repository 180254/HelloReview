package pl.p.lodz.iis.hr.utils;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Util service for operation on hibernate proxy.
 */
public final class ProxyUtils {

    private ProxyUtils() {
    }

    /**
     * Java instanceof operator may fail on hibernate proxy.<br/>
     * More info:
     * https://blog.oio.de/2010/09/24/instanceof-fails-with-hibernate-lazy-loading-and-entity-class-hierarchy/
     */
    public static boolean isInstanceOf(Object object, Class<?> clazz) {
        return ((Class<?>) Hibernate.getClass(object)).isAssignableFrom(clazz);
    }

    /**
     * Solution to deproxy lazy loaded hibernate entity.<br/>
     * More info:
     * http://www.javablog.fr/javahibernate-converting-hibernate-proxy-to-real-object-omrlazyoader.html"
     *
     * @param entity entity which is maybe proxy
     * @param <T>    entity class
     * @return unproxied entity
     */
    @SuppressWarnings("unchecked")
    public static <T> T initializeAndUnproxy(T entity) {
        if (entity instanceof HibernateProxy) {
            Hibernate.initialize(entity);
            return (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }
        return entity;
    }

    /**
     * Same as initializeAndUnproxy but for collection.
     *
     * @param entities collections of entities which are maybe proxy
     * @param <T>      entity class
     * @return list of unproxied entities
     */
    public static <T> List<T> unproxyCollection(Collection<T> entities) {
        List<T> objects = new ArrayList<>(entities.size());

        for (T entity : entities) {
            objects.add(initializeAndUnproxy(entity));
        }

        return objects;
    }
}
