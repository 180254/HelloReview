package pl.p.lodz.iis.hr.services;

import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.models.courses.Commission;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class GHTaskScheduler {

    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public void registerClone(Commission commission) {
        executor.submit(new GHTaskClone(commission));
    }

    /**
     * @param repoName simple repo name (ex "test", not "user/text")
     */
    public void registerDelete(String repoName) {
        executor.submit(new GHTaskDelete(repoName));
    }

    public void registerDirectoryRemove(String path) {
        executor.submit(new GHTaskDirRemove(path));
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

    public long getApproxNumberOfSubmittedTasks() {
        if (executor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor executorT = (ThreadPoolExecutor) executor;
            return executorT.getTaskCount();
        }

        return -1L;
    }
}
