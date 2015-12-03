package pl.p.lodz.iis.hr.configuration;

import org.hibernate.EmptyInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see HibernateStatConfiguration
 */
class HibernateStatInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = 2072241614985301859L;
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateStatInterceptor.class);

    private final ThreadLocal<Long> queryCount = new ThreadLocal<>();

    public void startCounter() {
        queryCount.set(0L);
    }

    public Long getQueryCount() {
        return queryCount.get();
    }

    public void clearCounter() {
        queryCount.remove();
    }

    @Override
    public String onPrepareStatement(String sql) {
        Long count = queryCount.get();
        if (count != null) {
            queryCount.set(count + 1L);
        }
        //LOGGER.info(sql);
        return super.onPrepareStatement(sql);
    }
}
