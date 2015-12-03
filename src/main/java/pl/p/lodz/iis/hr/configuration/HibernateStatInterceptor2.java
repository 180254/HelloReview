package pl.p.lodz.iis.hr.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see HibernateStatConfiguration
 */
class HibernateStatInterceptor2 implements AsyncHandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateStatInterceptor2.class);

    private final ThreadLocal<Long> time = new ThreadLocal<>();
    private final HibernateStatInterceptor statisticsInterceptor;

    HibernateStatInterceptor2(HibernateStatInterceptor statInterceptor) {
        this.statisticsInterceptor = statInterceptor;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        time.set(System.currentTimeMillis());
        statisticsInterceptor.startCounter();
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {

        Long queryCount = statisticsInterceptor.getQueryCount();
        modelAndView.addObject("_queryCount", queryCount);
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

        long duration = System.currentTimeMillis() - time.get();
        Long queryCount = statisticsInterceptor.getQueryCount();
        statisticsInterceptor.clearCounter();
        time.remove();

        LOGGER.debug("[Time: {} ms] [Queries: {}] {} {}",
                duration, queryCount, request.getMethod(), request.getRequestURI());
    }

    @Override
    public void afterConcurrentHandlingStarted(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        //concurrent handling cannot be supported here
        statisticsInterceptor.clearCounter();
        time.remove();
    }
}
