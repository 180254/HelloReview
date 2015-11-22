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
import pl.p.lodz.iis.hr.exceptions.GHCommunicationException;
import pl.p.lodz.iis.hr.models.courses.Commission;
import pl.p.lodz.iis.hr.models.courses.CommissionStatus;
import pl.p.lodz.iis.hr.repositories.CommissionRepository;
import pl.p.lodz.iis.hr.services.GHTaskScheduler;
import pl.p.lodz.iis.hr.utils.GHExecutor;

import java.util.*;
import java.util.stream.Collectors;

@Controller
class MStatsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MStatsController.class);

    private final GitHub gitHubFail;
    private final GHTaskScheduler ghTaskScheduler;
    private final CommissionRepository commissionRepository;
    private final Cache okHttpCache;

    @Autowired
    MStatsController(@Qualifier("ghFail") GitHub gitHubFail,
                     GHTaskScheduler ghTaskScheduler,
                     CommissionRepository commissionRepository,
                     Cache okHttpCache) {
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


        model.addAttribute("submitted", ghTaskScheduler.getApproxNumberOfSubmittedTasks());
        model.addAttribute("notCompleted", ghTaskScheduler.getApproxNumberOfScheduledTasks());
        model.addAttribute("processing", commissionRepository.findByStatus(CommissionStatus.PROCESSING).size());

        long okCacheHitPercent = Math.round(
                ((double) okHttpCache.getHitCount() / (double) okHttpCache.getRequestCount()) * 100.0
        );
        model.addAttribute("okCacheHitPercent", okCacheHitPercent);

        return "m-stats";
    }

    @RequestMapping(
            value = "/m/stats/junkclean",
            method = RequestMethod.GET)
    public String junkClean() {
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
            LOGGER.error("Exception while junk clean", e);
        }

        return "redirect:/m/stats";
    }
}
