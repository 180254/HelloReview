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
import pl.p.lodz.iis.hr.repositories.CommissionRepository;
import pl.p.lodz.iis.hr.services.GHTaskScheduler;
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
    public static final double TO_PERCENT = 100.0;

    private final AppConfig appConfig;
    private final GitHub gitHubFail;
    private final GHTaskScheduler ghTaskScheduler;
    private final CommissionRepository commissionRepository;
    private final Cache okHttpCache;


    @Autowired
    MStatsController(AppConfig appConfig,
                     @Qualifier("ghFail") GitHub gitHubFail,
                     GHTaskScheduler ghTaskScheduler,
                     CommissionRepository commissionRepository,
                     Cache okHttpCache) {
        this.appConfig = appConfig;
        this.gitHubFail = gitHubFail;
        this.ghTaskScheduler = ghTaskScheduler;
        this.commissionRepository = commissionRepository;
        this.okHttpCache = okHttpCache;
    }

    @RequestMapping(
            value = "/m/stats",
            method = RequestMethod.GET)
    public String stats(Model model) {

        try {
            GHExecutor.ex(() -> {
                GHRateLimit rateLimit = gitHubFail.getRateLimit();
                model.addAttribute("rateLimit", rateLimit);
            });
        } catch (GHCommunicationException e) {
            model.addAttribute("rateLimit", e);
        }

        long okCacheHitPercent = Math.round(
                ((double) okHttpCache.getHitCount() / (double) okHttpCache.getRequestCount()) * TO_PERCENT
        );
        model.addAttribute("okCacheHitPercent", okCacheHitPercent);

        try {
            GHExecutor.ex(() -> {

                Collection<GHRepository> gitRepos = gitHubFail.getMyself().getRepositories().values();
                List<Commission> dbRepos = commissionRepository.findAll();

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

                model.addAttribute("junkRepo", junkRepo.size() - 1);
            });
        } catch (GHCommunicationException e) {
            model.addAttribute("junkRepo", e);
        }

        String tempDir = appConfig.getGeneralConfig().getTempDir();
        try (DirectoryStream<Path> stream = getDirectoryStream(tempDir)) {

            int[] counter = {0};
            stream.forEach(p -> counter[0] += 1);
            model.addAttribute("junkTempDir", counter[0]);

        } catch (IOException e) {
            model.addAttribute("junkTempDir", e);
        }

        model.addAttribute("submitted", ghTaskScheduler.getApproxNumberOfSubmittedTasks());
        model.addAttribute("notCompleted", ghTaskScheduler.getApproxNumberOfScheduledTasks());
        model.addAttribute("processing", commissionRepository.findByStatus(CommissionStatus.PROCESSING).size());

        return "m-stats";
    }

    @RequestMapping(
            value = "/m/stats/junkclean/repo",
            method = RequestMethod.GET)
    public String junkCleanRepo() {
        try {
            GHExecutor.ex(() -> {

                Collection<GHRepository> gitRepos = gitHubFail.getMyself().getRepositories().values();
                List<Commission> dbRepos = commissionRepository.findAll();

                Map<String, Commission> repoUrlToComm = new HashMap<>(10);
                dbRepos.stream().forEach(comm -> repoUrlToComm.put(comm.getGhUrl(), comm));

                gitRepos.stream()
                        .filter(r -> (repoUrlToComm.get(r.getHtmlUrl().toString()) == null) // doesn't exist in db
                                || repoUrlToComm.get(r.getHtmlUrl().toString()).getReview().isClosed()) // or is closed
                        .filter(r -> !r.getName().equals("fix"))
                        .forEach(r -> ghTaskScheduler.registerDelete(r.getName()));
            });
        } catch (GHCommunicationException e) {
            LOGGER.error("Exception while junk repo clean", e);
        }

        return "redirect:/m/stats";
    }

    @RequestMapping(
            value = "/m/stats/junkclean/tempdir",
            method = RequestMethod.GET)
    public String junkCleanTempDir() {

        String tempDir = appConfig.getGeneralConfig().getTempDir();
        try (DirectoryStream<Path> stream = getDirectoryStream(tempDir)) {

            stream.forEach(p -> ghTaskScheduler.registerDirectoryRemove(p.toAbsolutePath().toString()));

        } catch (IOException e) {
            LOGGER.error("Exception while scheduling junk tempdir clean", e);
        }

        return "redirect:/m/stats";
    }

    private DirectoryStream<Path> getDirectoryStream(String dir) throws IOException {
        DirectoryStream.Filter<? super Path> isDirectoryFilter = entry -> entry.toFile().isDirectory();
        return Files.newDirectoryStream(Paths.get(dir), isDirectoryFilter);
    }
}
