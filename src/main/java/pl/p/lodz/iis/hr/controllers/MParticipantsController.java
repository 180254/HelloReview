package pl.p.lodz.iis.hr.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.p.lodz.iis.hr.configuration.Long2;
import pl.p.lodz.iis.hr.exceptions.*;
import pl.p.lodz.iis.hr.models.courses.Course;
import pl.p.lodz.iis.hr.models.courses.Participant;
import pl.p.lodz.iis.hr.repositories.CourseRepository;
import pl.p.lodz.iis.hr.repositories.ParticipantRepository;
import pl.p.lodz.iis.hr.services.FieldValidator;
import pl.p.lodz.iis.hr.services.LocaleService;
import pl.p.lodz.iis.hr.services.ResCommonService;

import java.util.Collections;
import java.util.List;

@Controller
class MParticipantsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MParticipantsController.class);

    private final ResCommonService resCommonService;
    private final CourseRepository courseRepository;
    private final ParticipantRepository participantRepository;
    private final FieldValidator fieldValidator;
    private final LocaleService localeService;

    @Autowired
    MParticipantsController(ResCommonService resCommonService,
                            CourseRepository courseRepository,
                            ParticipantRepository partiRepository,
                            FieldValidator fieldValidator,
                            LocaleService localeService) {
        this.resCommonService = resCommonService;
        this.courseRepository = courseRepository;
        this.participantRepository = partiRepository;
        this.fieldValidator = fieldValidator;
        this.localeService = localeService;
    }

    @RequestMapping(
            value = "/m/courses/{courseID}/participants",
            method = RequestMethod.GET)
    @Transactional
    public String list(@PathVariable Long2 courseID,
                       Model model)
            throws ResourceNotFoundException {

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
                          Model model)
            throws ResourceNotFoundException {

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
                                 @ModelAttribute("participant-github-name") String gitHubName)
            throws ResourceNotFoundRestException, FieldValidationRestException {

        Course course = resCommonService.getOneForRest(courseRepository, courseID.get());
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
            throw new FieldValidationRestException(allErrors);
        }

        LOGGER.debug("Participant added: {}", participant);
        participantRepository.save(participant);

        return localeService.getAsList("m.participants.add.done");
    }

    @RequestMapping(
            value = "/m/courses/participants/delete",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> delete(@ModelAttribute("id") Long2 participantID)
            throws ResourceNotFoundRestException, OtherRestProcessingException {

        Participant participant = resCommonService.getOneForRest(participantRepository, participantID.get());

        if (!participant.getCommissions().isEmpty()) {
            throw new OtherRestProcessingException("m.participants.delete.cannot.as.comm.exist");
        }

        LOGGER.debug("Participant deleted: {}", participant);
        participantRepository.delete(participant);

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
            throws ResourceNotFoundRestException, NotUniqueNameException, FieldValidationRestException {

        Participant participant = resCommonService.getOneForRest(participantRepository, participantID.get());

        if (participantRepository.findByCourseAndName(participant.getCourse(), newName) != null) {
            throw new NotUniqueNameException();
        }

        fieldValidator.validateFieldRestEx(
                new Participant(null, newName, null),
                "name",
                localeService.get("m.participants.add.validation.prefix.participant.name")
        );

        LOGGER.debug("Participant {} name changed to {}", participant, newName);
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
                                         @ModelAttribute("pk") Long2 participantID)
            throws ResourceNotFoundRestException, NotUniqueNameException, FieldValidationRestException {

        Participant participant = resCommonService.getOneForRest(participantRepository, participantID.get());

        if (participantRepository.findByCourseAndGitHubName(participant.getCourse(), newGitHubName) != null) {
            throw new NotUniqueNameException();
        }

        fieldValidator.validateFieldRestEx(
                new Participant(null, null, newGitHubName),
                "gitHubName",
                localeService.get("m.participants.add.validation.prefix.github.name")
        );

        LOGGER.debug("Participant {} GitHub name changed to {}", participant, newGitHubName);
        participant.setGitHubName(newGitHubName);
        participantRepository.save(participant);

        return localeService.getAsList("m.participants.rename.github.name.done");
    }
}
