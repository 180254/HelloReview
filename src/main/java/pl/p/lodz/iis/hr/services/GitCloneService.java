package pl.p.lodz.iis.hr.services;

import org.kohsuke.github.GHRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.configuration.appconfig.AppConfig;
import pl.p.lodz.iis.hr.models.response.ReviewResponse;
import pl.p.lodz.iis.hr.repositories.ReviewResponseRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class GitCloneService {

    @Autowired private AppConfig appConfig;
    @Autowired private ReviewResponseRepository reviewResponseRepository;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public void registerCloneJob(ReviewResponse reviewResponse, GHRepository ghRepository) {
        Runnable gitCloneTask = new GitCloneTask(
                appConfig, reviewResponseRepository,
                reviewResponse, ghRepository);

        executor.submit(gitCloneTask);
    }
}
