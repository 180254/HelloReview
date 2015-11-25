package pl.p.lodz.iis.hr.services;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

/**
 * Java instanceof operator may fail on hibernate proxy.<br/>
 * More info: https://blog.oio.de/2010/09/24/instanceof-fails-with-hibernate-lazy-loading-and-entity-class-hierarchy/
 */
@Service
public class InstanceOfChecker {

    public boolean check(Object object, Class<?> clazz) {
        Hibernate.initialize(object);
        return ((Class<?>) Hibernate.getClass(object)).isAssignableFrom(clazz);
    }

}
