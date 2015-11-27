package pl.p.lodz.iis.hr.services;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

/**
 * Util service for operation on hibernate proxy.
 */
@Service
public class ProxyService {

    /**
     * Java instanceof operator may fail on hibernate proxy.<br/>
     * More info:
     * https://blog.oio.de/2010/09/24/instanceof-fails-with-hibernate-lazy-loading-and-entity-class-hierarchy/
     */
    public boolean isInstanceOf(Object object, Class<?> clazz) {
        return ((Class<?>) Hibernate.getClass(object)).isAssignableFrom(clazz);
    }

}
