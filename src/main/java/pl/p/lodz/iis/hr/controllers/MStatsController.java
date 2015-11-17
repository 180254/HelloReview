package pl.p.lodz.iis.hr.controllers;

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
import pl.p.lodz.iis.hr.repositories.CommissionRepository;
import pl.p.lodz.iis.hr.services.GitExecuteService;
import pl.p.lodz.iis.hr.utils.GHExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
class MStatsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MStatsController.class);

    @Autowired @Qualifier("ghFail") private GitHub gitHubFail;
    @Autowired private GitExecuteService gitExecuteService;
    @Autowired private AppConfig appConfig;
    @Autowired private CommissionRepository commissionRepository;

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

                List<String> dbReposUrl = dbRepos.stream()
                        .map(Commission::getGhUrl)
                        .collect(Collectors.toList());

                Collection<String> diff = new ArrayList<>(10);
                diff.addAll(gitRepoUrls);
                diff.removeAll(dbReposUrl);

                model.addAttribute("junkRepo", diff.size() - 1);
            });
        } catch (GHCommunicationException e) {
            model.addAttribute("junkRepo", e);
        }

        model.addAttribute("submitted", gitExecuteService.getApproxNumberOfSubmittedTasks());
        model.addAttribute("notCompleted", gitExecuteService.getApproxNumberOfNotCompletedTasks());

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

                List<String> dbReposUrl = dbRepos.stream()
                        .map(Commission::getGhUrl)
                        .collect(Collectors.toList());

                gitRepos.stream()
                        .filter(r -> !dbReposUrl.contains(r.getHtmlUrl().toString()))
                        .filter(r -> !r.getName().equals("fix"))
                        .forEach(r -> gitExecuteService.registerDelete(r.getName()));
            });
        } catch (GHCommunicationException e) {
            LOGGER.error("Exception while junk clean", e);
        }

        return "redirect:/m/stats";
    }
}
