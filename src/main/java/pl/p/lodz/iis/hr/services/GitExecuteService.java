package pl.p.lodz.iis.hr.services;

import org.kohsuke.github.GHRepository;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.models.courses.Commission;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class GitExecuteService {

    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    public void registerCloneJob(Commission commission, GHRepository ghRepository) {
        Runnable gitCloneTask = new GitExecuteCloneTask(commission, ghRepository);
        executor.submit(gitCloneTask);
    }

    public void registerDelete(String repoName) {
        Runnable gitDeleteTask = new GitExecuteDeleteTask(repoName);
        executor.submit(gitDeleteTask);
    }

    public int getApproxNumberOfNotCompletedTasks() {
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
