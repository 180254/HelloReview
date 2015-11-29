package pl.p.lodz.iis.hr.services;

import com.squareup.okhttp.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.models.courses.Commission;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * GHTask* classes should not be executed in same thread as principal class.<br/>
 * They executing lot of time, and should be scheduled in another thread.<br/>
 * THis class is to schedule these tasks to be executed.
 */
@Service
public class GHTaskScheduler {

    private final ExecutorService executor = Executors.newFixedThreadPool(3);
    private final Cache okCache;

    @Autowired
    public GHTaskScheduler(Cache okHttpCache) {
        this.okCache = okHttpCache;
    }

    public void registerClone(Commission commission) {
        executor.submit(new GHTaskClone(commission));
    }

    /**
     * @param repoSimpleName simple repo name (ex "test", not "user/text")
     */
    public void registerDelete(String repoSimpleName) {
        executor.submit(new GHTaskDelete(repoSimpleName));
    }

    public void registerDirectoryRemove(String path) {
        executor.submit(new GHTaskDirRemove(path));
    }

    public void registerOkCacheClean() {
        executor.submit(new GHTaskOkCacheClean(okCache));
    }

    public int getApproxNumberOfScheduledTasks() {
        if (executor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor executorT = (ThreadPoolExecutor) executor;
            int queued = executorT.getQueue().size();
            int active = executorT.getActiveCount();
            return queued + active;
        }

        return -1;
    }

    public long getApproxNumberOfSubmittedTasksCnt() {
        if (executor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor executorT = (ThreadPoolExecutor) executor;
            return executorT.getTaskCount();
        }

        return -1L;
    }

    public boolean shouldRetryButtonBeEnabled() {
        return getApproxNumberOfScheduledTasks() == 0;
    }

    public boolean shouldStatsCleanFunctionsBeEnabled() {
        return getApproxNumberOfScheduledTasks() == 0;
    }
}
