package pl.p.lodz.iis.hr.services;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.configuration.appconfig.AppConfig;
import pl.p.lodz.iis.hr.models.response.ReviewResponse;
import pl.p.lodz.iis.hr.repositories.ReviewResponseRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class GitExecuteService {

    @Autowired private AppConfig appConfig;
    @Autowired private CredentialsProvider jGitCredentials;
    @Autowired private ReviewResponseRepository responseRepository;
    @Autowired private GitHub gitHub;

    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    public void registerCloneJob(ReviewResponse reviewResponse, GHRepository ghRepository) {

        Runnable gitCloneTask = new GitCloneTask(
                appConfig,
                gitHub,
                jGitCredentials,
                responseRepository,

                reviewResponse,
                ghRepository
        );

        executor.submit(gitCloneTask);
    }

    public void registerDelete(String repoName) {

        Runnable gitDeleteTask = new GitDeleteTask(
                gitHub,
                appConfig,
                repoName
        );

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
