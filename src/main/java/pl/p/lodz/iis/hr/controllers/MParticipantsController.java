package pl.p.lodz.iis.hr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.p.lodz.iis.hr.configuration.Long2;
import pl.p.lodz.iis.hr.models.courses.Course;
import pl.p.lodz.iis.hr.models.courses.Participant;
import pl.p.lodz.iis.hr.repositories.CourseRepository;
import pl.p.lodz.iis.hr.repositories.ParticipantRepository;
import pl.p.lodz.iis.hr.services.FieldValidator;
import pl.p.lodz.iis.hr.services.LocaleService;
import pl.p.lodz.iis.hr.services.ResCommonService;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@Controller
class MParticipantsController {

    private final ResCommonService resCommonService;
    private final CourseRepository courseRepository;
    private final ParticipantRepository participantRepository;
    private final FieldValidator fieldValidator;
    private final LocaleService localeService;

    @Autowired
    MParticipantsController(ResCommonService resCommonService,
                            CourseRepository courseRepository,
                            ParticipantRepository participantRepository,
                            FieldValidator fieldValidator,
                            LocaleService localeService) {
        this.resCommonService = resCommonService;
        this.courseRepository = courseRepository;
        this.participantRepository = participantRepository;
        this.fieldValidator = fieldValidator;
        this.localeService = localeService;
    }

    @Autowired

    @RequestMapping(
            value = "/m/courses/{courseID}/participants",
            method = RequestMethod.GET)
    @Transactional
    public String list(@PathVariable Long2 courseID,
                       Model model) {

        Course course = resCommonService.getOne(courseRepository, courseID.get());
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
                          Model model) {

        Participant participant = resCommonService.getOne(participantRepository, participantID.get());
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
                                 HttpServletResponse response) {

        Course course = resCommonService.getOneForJSON(courseRepository, courseID.get());

        if (course == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return localeService.getAsList("NoResource");
        }

        Participant participant = new Participant(course, name, gitHubName);

        String namePrefix = localeService.get("m.participants.add.validation.prefix.participant.name");
        String gitHubNamePrefix = localeService.get("m.participants.add.validation.prefix.github.name");

        List<String> allErrors = fieldValidator.validateFields(
                participant,
                new String[]{"name", "gitHubName"},
                new String[]{namePrefix, gitHubNamePrefix}
        );

        if (participantRepository.findByCourseAndName(course, name) != null) {
            allErrors.add(String.format("%s %s", namePrefix, localeService.get("UniqueName")));
        }

        if (participantRepository.findByCourseAndGitHubName(course, gitHubName) != null) {
            allErrors.add(String.format("%s %s", gitHubNamePrefix, localeService.get("UniqueName")));
        }

        if (!allErrors.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return allErrors;
        }

        participantRepository.save(participant);

        return localeService.getAsList("m.participants.add.done");
    }

    @RequestMapping(
            value = "/m/courses/participants/delete",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> delete(@ModelAttribute("id") Long2 participantID,
                               HttpServletResponse response) {

        Participant participant = resCommonService.getOneForJSON(participantRepository, participantID.get());

        if (participant == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return localeService.getAsList("NoResource");
        }

        if (!participant.getCommissions().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return localeService.getAsList("m.participants.delete.cannot.as.comm.exist");

        }

        participantRepository.delete(participantID.get());
        return localeService.getAsList("m.participants.delete.done");
    }

    @RequestMapping(
            value = "/m/courses/participants/rename/name",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> renameName(@ModelAttribute("value") String newName,
                                   @ModelAttribute("pk") Long2 participantID,
                                   HttpServletResponse response) {

        Participant participant = resCommonService.getOneForJSON(participantRepository, participantID.get());

        if (participant == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return localeService.getAsList("NoResource");
        }

        if (participantRepository.findByCourseAndName(participant.getCourse(), newName) != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return localeService.getAsList("UniqueName");
        }

        List<String> nameErrors = fieldValidator.validateField(
                new Participant(null, newName, null),
                "name",
                localeService.get("m.participants.add.validation.prefix.participant.name")
        );

        if (!nameErrors.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return nameErrors;
        }

        participant.setName(newName);
        participantRepository.save(participant);

        return localeService.getAsList("m.participants.rename.participant.name.done");
    }

    @RequestMapping(
            value = "/m/courses/participants/rename/githubname",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> renameGitHubName(@ModelAttribute("value") String newGitHubName,
                                         @ModelAttribute("pk") Long2 participantID,
                                         HttpServletResponse response) {

        Participant participant = resCommonService.getOneForJSON(participantRepository, participantID.get());

        if (participant == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return localeService.getAsList("NoResource");
        }

        if (participantRepository.findByCourseAndGitHubName(participant.getCourse(), newGitHubName) != null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return localeService.getAsList("UniqueName");
        }

        List<String> gitHubNameErrors = fieldValidator.validateField(
                new Participant(null, null, newGitHubName),
                "gitHubName",
                localeService.get("m.participants.add.validation.prefix.github.name")
        );

        if (!gitHubNameErrors.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return gitHubNameErrors;
        }

        participant.setGitHubName(newGitHubName);
        participantRepository.save(participant);

        return localeService.getAsList("m.participants.rename.github.name.done");
    }
}
