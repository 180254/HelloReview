package pl.p.lodz.iis.hr.controllers;

import org.kohsuke.github.GHRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.p.lodz.iis.hr.appconfig.AppConfig;
import pl.p.lodz.iis.hr.configuration.HttpConnectorWithCache;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.services.GHTaskScheduler;
import pl.p.lodz.iis.hr.services.RepositoryProvider;
import pl.p.lodz.iis.hr.services.StatsService;
import pl.p.lodz.iis.hr.utils.ByteUnit;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.List;

@Controller
class MStatsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MStatsController.class);

    private final AppConfig appConfig;
    private final GHTaskScheduler ghTaskScheduler;
    private final RepositoryProvider repositoryProvider;
    private final HttpConnectorWithCache httpConnectorWithCache;
    private final StatsService statsService;

    @Autowired
    MStatsController(AppConfig appConfig,
                     GHTaskScheduler ghTaskScheduler,
                     RepositoryProvider repositoryProvider,
                     HttpConnectorWithCache httpConnectorCache,
                     StatsService statsService) {
        this.appConfig = appConfig;
        this.ghTaskScheduler = ghTaskScheduler;
        this.repositoryProvider = repositoryProvider;
        this.httpConnectorWithCache = httpConnectorCache;
        this.statsService = statsService;
    }

    @RequestMapping(
            value = "/m/stats",
            method = RequestMethod.GET)
    public String stats(Model model) {

        model.addAttribute("ghRateLimit", statsService.getGhRateLimit());
        model.addAttribute("cacheEnabled", statsService.isCacheEnabled());
        model.addAttribute("cacheSizeMB", ByteUnit.BYTE.toMiB((double) statsService.getCacheSizeBytes()));
        model.addAttribute("cacheMaxSizeMB", ByteUnit.BYTE.toMiB((double) statsService.getCacheMaxSizeBytes()));
        model.addAttribute("cacheHitPercent", statsService.getCacheHitPercent());
        model.addAttribute("junkRepoCnt", statsService.getListOfJunkRepos().size());
        model.addAttribute("junkTempDirCnt", statsService.getNumberOfJunkTempDirs());
        model.addAttribute("submittedTasksCnt", ghTaskScheduler.getApproxNumberOfSubmittedTasksCnt());
        model.addAttribute("scheduledTasksCnt", ghTaskScheduler.getApproxNumberOfScheduledTasks());
        model.addAttribute("processingTasksCnt", statsService.getNumberOfProcessingTasksCnt());

        return "m-stats";
    }

    @RequestMapping(
            value = "/m/stats/clean/cache-dir",
            method = RequestMethod.GET)
    public String cleanCacheDir() {

        LOGGER.info("cleanCacheDir executed");

        if (ghTaskScheduler.shouldStatsCleanFunctionsBeEnabled()) {
            ghTaskScheduler.registerOkCacheClean();
        }

        return "redirect:/m/stats";
    }

    @RequestMapping(
            value = "/m/stats/clean/temp-dir",
            method = RequestMethod.GET)
    public String junkCleanTemp() {

        LOGGER.info("junkCleanTemp executed");

        if (ghTaskScheduler.shouldStatsCleanFunctionsBeEnabled()) {

            String tempDir = appConfig.getGeneralConfig().getTempDir();
            try (DirectoryStream<Path> stream = statsService.getDirectoryStream(tempDir)) {

                stream.forEach(p -> ghTaskScheduler.registerDirectoryRemove(p.toAbsolutePath().toString()));

            } catch (IOException e) {
                LOGGER.warn("Unable to junkCleanTemp()", e);
            }

        }

        return "redirect:/m/stats";
    }

    @RequestMapping(
            value = "/m/stats/clean/junk-repo",
            method = RequestMethod.GET)
    @Transactional
    public String cleanJunkRepo() {

        LOGGER.info("cleanJunkRepo executed");

        if (ghTaskScheduler.shouldStatsCleanFunctionsBeEnabled()) {

            List<GHRepository> junkRepos = statsService.getListOfJunkRepos();

            if (!junkRepos.isEmpty()) {
                junkRepos.forEach(r -> ghTaskScheduler.registerDelete(r.getName()));
                List<Review> closedReviews = repositoryProvider.review().findByClosedIsNotNull();
                closedReviews.stream().forEach(r -> r.setCleaned(true));
                repositoryProvider.review().save(closedReviews);
            }
        }

        return "redirect:/m/stats";
    }

    @RequestMapping(
            value = "/m/stats/cache-switch",
            method = RequestMethod.GET)
    public String cacheSwitch() {

        LOGGER.info("cacheSwitch executed, new state = {}", !httpConnectorWithCache.isCacheEnabled());

        if (httpConnectorWithCache.isCacheEnabled()) {
            httpConnectorWithCache.disableCache();
        } else {
            httpConnectorWithCache.enableCache();
        }

        return "redirect:/m/stats";
    }

}
