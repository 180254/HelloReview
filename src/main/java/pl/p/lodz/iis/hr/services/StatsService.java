package pl.p.lodz.iis.hr.services;

import com.google.common.collect.Iterators;
import com.squareup.okhttp.Cache;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.appconfig.AppConfig;
import pl.p.lodz.iis.hr.configuration.HttpConnectorWithCache;
import pl.p.lodz.iis.hr.exceptions.GHCommunicationException;
import pl.p.lodz.iis.hr.models.courses.Commission;
import pl.p.lodz.iis.hr.models.courses.CommissionStatus;
import pl.p.lodz.iis.hr.utils.GHExecutor;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatsService.class);
    private static final double TO_PERCENT_MULTIPLIER = 100.0;

    private final AppConfig appConfig;
    private final GitHub gitHubFail;
    private final Cache okCache;
    private final HttpConnectorWithCache httpConnectorWithCache;
    private final RepositoryProvider repositoryProvider;

    @Autowired
    StatsService(AppConfig appConfig,
                 @Qualifier("ghFail") GitHub gitHubFail,
                 Cache okCache,
                 HttpConnectorWithCache httpConnectorCache,
                 RepositoryProvider repositoryProvider) {
        this.appConfig = appConfig;
        this.gitHubFail = gitHubFail;
        this.okCache = okCache;
        this.httpConnectorWithCache = httpConnectorCache;
        this.repositoryProvider = repositoryProvider;
    }

    public String getGhRateLimit() {
        try {
            return GHExecutor.ex(() -> gitHubFail.getRateLimit().toString());
        } catch (GHCommunicationException e) {
            return e.toString();
        }
    }

    public boolean isCacheEnabled() {
        return httpConnectorWithCache.isCacheEnabled();
    }

    public long getCacheSizeBytes() {
        try {
            return okCache.getSize();

        } catch (IOException e) {
            LOGGER.warn("Unable to getCacheSizeBytes()", e);
            return -1L;
        }
    }

    public long getCacheMaxSizeBytes() {
        return okCache.getMaxSize();
    }

    public long getCacheHitPercent() {
        return Math.round(
                ((double) okCache.getHitCount() / (double) okCache.getRequestCount()) * TO_PERCENT_MULTIPLIER
        );
    }

    public List<GHRepository> getListOfJunkRepos() {

        try {
            return GHExecutor.ex(() -> {

                Collection<GHRepository> gitRepos = gitHubFail.getMyself().getRepositories().values();
                List<Commission> dbRepos = repositoryProvider.commission().findAll();

                Map<String, Commission> repoUrlToComm = new HashMap<>(10);
                dbRepos.stream().forEach(comm -> repoUrlToComm.put(comm.getGhUrl(), comm));

                Predicate<GHRepository> doesNotExistInDb = (GHRepository r) ->
                        (repoUrlToComm.get(r.getHtmlUrl().toString()) == null);

                Predicate<GHRepository> relatedReviewIsClosed = (GHRepository r) ->
                        repoUrlToComm.get(r.getHtmlUrl().toString()).getReview().isClosed();

                return gitRepos.stream()
                        .filter(r -> doesNotExistInDb.test(r) || relatedReviewIsClosed.test(r))
                        .filter(r -> !r.getName().equals("fix"))
                        .collect(Collectors.toList());
            });

        } catch (GHCommunicationException e) {
            LOGGER.warn("Unable to getListOfJunkRepos()", e);
            return Collections.emptyList();
        }
    }

    public long getNumberOfJunkTempDirs() {
        String tempDir = appConfig.getGeneralConfig().getTempDir();
        try (DirectoryStream<Path> stream = getDirectoryStream(tempDir)) {
            return (long) Iterators.size(stream.iterator());

        } catch (IOException e) {
            LOGGER.warn("Unable to getNumberOfJunkTempDirs()", e);
            return -1L;
        }
    }

    public long getNumberOfProcessingTasksCnt() {
        return (long) repositoryProvider.commission().findByStatus(CommissionStatus.PROCESSING).size();
    }

    public DirectoryStream<Path> getDirectoryStream(String dir) throws IOException {
        DirectoryStream.Filter<? super Path> isDirectoryFilter = entry -> entry.toFile().isDirectory();
        return Files.newDirectoryStream(Paths.get(dir), isDirectoryFilter);
    }
}

