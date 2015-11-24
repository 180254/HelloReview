package pl.p.lodz.iis.hr.utils;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

/**
 * The Default Comparator for classes implementing Comparable.
 *
 * @param <E> the type of the comparable objects.
 * @author Michael Belivanakis (michael.gr)
 */
public final class DefaultComparator<E extends Comparable<E>> implements Comparator<E>, Serializable {

    private static final long serialVersionUID = 74113594956172430L;

    /**
     * Get an instance of DefaultComparator for any type of Comparable.
     *
     * @param <T> the type of Comparable of interest.
     * @return an instance of DefaultComparator for comparing instances of the requested type.
     */
    public static <T extends Comparable<T>> Comparator<T> getInstance() {
        return new DefaultComparator<>();
    }

    private DefaultComparator() {
    }

    @Override
    public int compare(E o1, E o2) {
        if (Objects.equals(o1, o2)) {
            return 0;
        }
        if (o1 == null) {
            return 1;
        }
        if (o2 == null) {
            return -1;
        }
        return o1.compareTo(o2);
    }
}
