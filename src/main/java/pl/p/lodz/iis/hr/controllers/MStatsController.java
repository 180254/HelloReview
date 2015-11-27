package pl.p.lodz.iis.hr.controllers;

import com.squareup.okhttp.Cache;
import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.p.lodz.iis.hr.appconfig.AppConfig;
import pl.p.lodz.iis.hr.exceptions.GHCommunicationException;
import pl.p.lodz.iis.hr.models.courses.Commission;
import pl.p.lodz.iis.hr.models.courses.CommissionStatus;
import pl.p.lodz.iis.hr.services.GHTaskScheduler;
import pl.p.lodz.iis.hr.services.RepositoryProvider;
import pl.p.lodz.iis.hr.utils.GHExecutor;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Controller
class MStatsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MStatsController.class);
    private static final double TO_PERCENT_MULTIPLIER = 100.0;

    private final AppConfig appConfig;
    private final GitHub gitHubFail;
    private final GHTaskScheduler ghTaskScheduler;
    private final RepositoryProvider repositoryProvider;
    private final Cache okHttpCache;

    @Autowired
    MStatsController(AppConfig appConfig,
                     @Qualifier("ghFail") GitHub gitHubFail,
                     GHTaskScheduler ghTaskScheduler,
                     RepositoryProvider repositoryProvider,
                     Cache okHttpCache) {
        this.appConfig = appConfig;
        this.gitHubFail = gitHubFail;
        this.ghTaskScheduler = ghTaskScheduler;
        this.repositoryProvider = repositoryProvider;
        this.okHttpCache = okHttpCache;
    }

    @RequestMapping(
            value = "/m/stats",
            method = RequestMethod.GET)
    public String stats(Model model) {

        appendRateLimitToModel(model);
        appendOkCacheHitPercentToModel(model);
        appendJunkRepoCntToModel(model);
        appendJunkTempDirCntToModel(model);

        long submitted = ghTaskScheduler.getApproxNumberOfSubmittedTasks();
        int notCompleted = ghTaskScheduler.getApproxNumberOfScheduledTasks();
        int processing = repositoryProvider.commission().findByStatus(CommissionStatus.PROCESSING).size();

        model.addAttribute("submitted", submitted);
        model.addAttribute("notCompleted", notCompleted);
        model.addAttribute("processing", processing);

        return "m-stats";
    }


    private void appendRateLimitToModel(Model model) {
        try {
            GHExecutor.ex(() -> {
                GHRateLimit rateLimit = gitHubFail.getRateLimit();
                model.addAttribute("rateLimit", rateLimit);
            });
        } catch (GHCommunicationException e) {
            model.addAttribute("rateLimit", e);
        }
    }


    private void appendOkCacheHitPercentToModel(Model model) {
        long okCacheHitPercent = Math.round(
                ((double) okHttpCache.getHitCount() / (double) okHttpCache.getRequestCount()) * TO_PERCENT_MULTIPLIER
        );
        model.addAttribute("okCacheHitPercent", okCacheHitPercent);
    }

    private void appendJunkRepoCntToModel(Model model) {
        try {
            GHExecutor.ex(() -> {

                Collection<GHRepository> gitRepos = gitHubFail.getMyself().getRepositories().values();
                List<Commission> dbRepos = repositoryProvider.commission().findAll();

                List<String> gitRepoUrls = gitRepos.stream()
                        .map(r -> r.getHtmlUrl().toString())
                        .collect(Collectors.toList());

                List<String> dbNotClosedReposUrl = dbRepos.stream()
                        .filter(comm -> !comm.getReview().isClosed())
                        .map(Commission::getGhUrl)
                        .collect(Collectors.toList());

                Collection<String> junkRepo = new ArrayList<>(10);
                junkRepo.addAll(gitRepoUrls);
                junkRepo.removeAll(dbNotClosedReposUrl);

                model.addAttribute("junkRepoCnt", junkRepo.size() - 1);
            });
        } catch (GHCommunicationException e) {
            model.addAttribute("junkRepoCnt", e);
        }
    }

    private void appendJunkTempDirCntToModel(Model model) {
        String tempDir = appConfig.getGeneralConfig().getTempDir();
        try (DirectoryStream<Path> stream = getDirectoryStream(tempDir)) {

            int[] counter = {0};
            stream.forEach(p -> counter[0] += 1);
            model.addAttribute("junkTempDirCnt", counter[0]);

        } catch (IOException e) {
            model.addAttribute("junkTempDirCnt", e);
        }
    }

    @RequestMapping(
            value = "/m/stats/junk-clean/repo",
            method = RequestMethod.GET)
    public String junkCleanRepo() {
        try {
            GHExecutor.ex(() -> {

                Collection<GHRepository> gitRepos = gitHubFail.getMyself().getRepositories().values();
                List<Commission> dbRepos = repositoryProvider.commission().findAll();

                Map<String, Commission> repoUrlToComm = new HashMap<>(10);
                dbRepos.stream().forEach(comm -> repoUrlToComm.put(comm.getGhUrl(), comm));

                gitRepos.stream()
                        .filter(r -> (repoUrlToComm.get(r.getHtmlUrl().toString()) == null) // doesn't exist in db
                                || repoUrlToComm.get(r.getHtmlUrl().toString()).getReview().isClosed()) // or is closed
                        .filter(r -> !r.getName().equals("fix"))
                        .forEach(r -> ghTaskScheduler.registerDelete(r.getName()));
            });
        } catch (GHCommunicationException e) {
            LOGGER.warn("Exception while junk repo clean", e);
        }

        return "redirect:/m/stats";
    }

    @RequestMapping(
            value = "/m/stats/junk-clean/temp",
            method = RequestMethod.GET)
    public String junkCleanTemp() {

        String tempDir = appConfig.getGeneralConfig().getTempDir();
        try (DirectoryStream<Path> stream = getDirectoryStream(tempDir)) {

            stream.forEach(p -> ghTaskScheduler.registerDirectoryRemove(p.toAbsolutePath().toString()));

        } catch (IOException e) {
            LOGGER.warn("Exception while scheduling junk temp dir clean", e);
        }

        return "redirect:/m/stats";
    }

    private DirectoryStream<Path> getDirectoryStream(String dir) throws IOException {
        DirectoryStream.Filter<? super Path> isDirectoryFilter = entry -> entry.toFile().isDirectory();
        return Files.newDirectoryStream(Paths.get(dir), isDirectoryFilter);
    }
}
