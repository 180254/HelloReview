package pl.p.lodz.iis.hr.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.fragment.DOMSelectorFragmentSpec;
import pl.p.lodz.iis.hr.configuration.Long2;
import pl.p.lodz.iis.hr.exceptions.ErrorPageException;
import pl.p.lodz.iis.hr.exceptions.LocalizableErrorRestException;
import pl.p.lodz.iis.hr.exceptions.LocalizedErrorRestException;
import pl.p.lodz.iis.hr.models.courses.Course;
import pl.p.lodz.iis.hr.models.courses.Participant;
import pl.p.lodz.iis.hr.services.FieldValidator;
import pl.p.lodz.iis.hr.services.LocaleService;
import pl.p.lodz.iis.hr.services.RepositoryProvider;
import pl.p.lodz.iis.hr.services.ResCommonService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Controller
class MParticipantsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MParticipantsController.class);

    private final TemplateEngine templateEngine;
    private final ResCommonService resCommonService;
    private final RepositoryProvider repositoryProvider;
    private final FieldValidator fieldValidator;
    private final LocaleService localeService;

    @Autowired
    MParticipantsController(TemplateEngine templateEngine,
                            ResCommonService resCommonService,
                            RepositoryProvider repositoryProvider,
                            FieldValidator fieldValidator,
                            LocaleService localeService) {
        this.templateEngine = templateEngine;
        this.resCommonService = resCommonService;
        this.repositoryProvider = repositoryProvider;
        this.fieldValidator = fieldValidator;
        this.localeService = localeService;
    }

    @RequestMapping(
            value = "/m/courses/{courseID}/participants",
            method = RequestMethod.GET)
    @Transactional
    public String list(@PathVariable Long2 courseID,
                       Model model)
            throws ErrorPageException {

        Course course = resCommonService.getOne(repositoryProvider.course(), courseID.get());
        List<Participant> participants = course.getParticipants();

        model.addAttribute("course", course);
        model.addAttribute("participants", participants);
        model.addAttribute("newButton", true);
        model.addAttribute("addon_allParticipants", true);

        return "m-participants";
    }

    @RequestMapping(
            value = "/m/courses/participants/{participantID}",
            method = RequestMethod.GET)
    @Transactional
    public String listOne(@PathVariable Long2 participantID,
                          Model model)
            throws ErrorPageException {

        Participant participant = resCommonService.getOne(repositoryProvider.participant(), participantID.get());
        Course course = participant.getCourse();

        model.addAttribute("course", course);
        model.addAttribute("participants", Collections.singletonList(participant));
        model.addAttribute("newButton", false);
        model.addAttribute("addon_oneParticipant", true);

        return "m-participants";
    }

    @RequestMapping(
            value = "/m/courses/participants/add",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> kAddPOST(@ModelAttribute("course-id") Long2 courseID,
                                 @ModelAttribute("participant-name") String name,
                                 @ModelAttribute("participant-github-name") String gitHubName,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 ServletContext servletContext,
                                 Locale locale)
            throws LocalizedErrorRestException, LocalizableErrorRestException {

        Course course = resCommonService.getOneForRest(repositoryProvider.course(), courseID.get());
        Participant participant = new Participant(course, name, gitHubName);

        String namePrefix = localeService.get("m.participants.add.validation.prefix.participant.name");
        String gitHubNamePrefix = localeService.get("m.participants.add.validation.prefix.github.name");

        List<String> allErrors = fieldValidator.validateFields(
                participant,
                new String[]{"name", "gitHubName"},
                new String[]{namePrefix, gitHubNamePrefix}
        );

        if (repositoryProvider.participant().findByCourseAndName(course, name) != null) {
            allErrors.add(String.format("%s %s", namePrefix, localeService.get("UniqueName")));
        }

        if (repositoryProvider.participant().findByCourseAndGitHubName(course, gitHubName) != null) {
            allErrors.add(String.format("%s %s", gitHubNamePrefix, localeService.get("UniqueName")));
        }

        if (!allErrors.isEmpty()) {
            throw new LocalizedErrorRestException(allErrors);
        }

        LOGGER.debug("Participant added {}", participant);
        repositoryProvider.participant().save(participant);
        LOGGER.info("Added participant {}", participant);

        String msg = localeService.get("m.participants.add.done");

        WebContext ctx = new WebContext(request, response, servletContext, locale);
        ctx.setVariable("participants", Collections.singletonList(participant));
        String row = templateEngine.process("m-participants", ctx, new DOMSelectorFragmentSpec(".participant-one"));

        return Arrays.asList(msg, row);
    }

    @RequestMapping(
            value = "/m/courses/participants/delete",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> delete(@ModelAttribute("id") Long2 participantID)
            throws LocalizableErrorRestException {

        Participant participant = resCommonService.getOneForRest(repositoryProvider.participant(), participantID.get());

        if (!participant.getCommissionsAsAssessor().isEmpty()) {
            throw new LocalizableErrorRestException("m.participants.delete.cannot.as.comm.exist");
        }

        LOGGER.info("Participant deleted {}", participant);
        repositoryProvider.participant().delete(participant);

        return localeService.getAsList("m.participants.delete.done");
    }

    @RequestMapping(
            value = "/m/courses/participants/rename/name",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> renameName(@ModelAttribute("value") String newName,
                                   @ModelAttribute("pk") Long2 participantID)
            throws LocalizedErrorRestException, LocalizableErrorRestException {

        Participant participant = resCommonService.getOneForRest(repositoryProvider.participant(), participantID.get());

        if (repositoryProvider.participant().findByCourseAndName(participant.getCourse(), newName) != null) {
            throw LocalizableErrorRestException.notUniqueName();
        }

        fieldValidator.validateFieldRestEx(
                new Participant(null, newName, null),
                "name",
                localeService.get("m.participants.add.validation.prefix.participant.name")
        );

        LOGGER.info("Participant {} name changed to {}", participant, newName);
        participant.setName(newName);
        repositoryProvider.participant().save(participant);

        return localeService.getAsList("m.participants.rename.participant.name.done");
    }

    @RequestMapping(
            value = "/m/courses/participants/rename/github-name",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> renameGitHubName(@ModelAttribute("value") String newGitHubName,
                                         @ModelAttribute("pk") Long2 participantID)
            throws LocalizedErrorRestException, LocalizableErrorRestException {

        Participant participant = resCommonService.getOneForRest(repositoryProvider.participant(), participantID.get());

        if (repositoryProvider.participant()
                .findByCourseAndGitHubName(participant.getCourse(), newGitHubName) != null) {
            throw LocalizableErrorRestException.notUniqueName();
        }

        fieldValidator.validateFieldRestEx(
                new Participant(null, null, newGitHubName),
                "gitHubName",
                localeService.get("m.participants.add.validation.prefix.github.name")
        );

        LOGGER.info("Participant {} GitHub name changed to {}", participant, newGitHubName);
        participant.setGitHubName(newGitHubName);
        repositoryProvider.participant().save(participant);

        return localeService.getAsList("m.participants.rename.github.name.done");
    }
}
