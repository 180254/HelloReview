package pl.p.lodz.iis.hr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.p.lodz.iis.hr.configuration.Long2;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;
import pl.p.lodz.iis.hr.models.courses.Course;
import pl.p.lodz.iis.hr.models.courses.Participant;
import pl.p.lodz.iis.hr.repositories.CourseRepository;
import pl.p.lodz.iis.hr.repositories.ParticipantRepository;
import pl.p.lodz.iis.hr.services.FieldValidateService;
import pl.p.lodz.iis.hr.services.LocaleService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static java.util.Collections.singletonList;

@Controller
class MParticipantsController {

    @Autowired private CourseRepository courseRepository;
    @Autowired private ParticipantRepository participantRepository;
    @Autowired private FieldValidateService fieldValidateService;
    @Autowired private LocaleService localeService;

    @RequestMapping(
            value = "/m/courses/{courseID}/participants",
            method = RequestMethod.GET)
    @Transactional
    public String list(@PathVariable Long2 courseID,
                       Model model) {

        if (!courseRepository.exists(courseID.get())) {
            throw new ResourceNotFoundException();
        }

        Course course = courseRepository.getOne(courseID.get());

        model.addAttribute("newButton", true);
        model.addAttribute("course", course);
        model.addAttribute("participants", course.getParticipants());

        model.addAttribute("addon_allParticipants", true);

        return "m-participants";
    }

    @RequestMapping(
            value = "/m/courses/participants/{participantID}",
            method = RequestMethod.GET)
    @Transactional
    public String listOne(@PathVariable Long2 participantID,
                          Model model) {

        if (!participantRepository.exists(participantID.get())) {
            throw new ResourceNotFoundException();
        }

        Participant participant = participantRepository.getOne(participantID.get());

        model.addAttribute("newButton", false);
        model.addAttribute("course", participant.getCourse());
        model.addAttribute("participants", singletonList(participant));

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

        if (!courseRepository.exists(courseID.get())) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.get("NoResource"));
        }

        Course course = courseRepository.getOne(courseID.get());
        Participant participant = new Participant(course, name, gitHubName);

        String namePrefix = localeService.get("m.participants.add.validation.prefix.participant.name");
        String gitHubNamePrefix = localeService.get("m.participants.add.validation.prefix.github.name");

        List<String> allErrors = fieldValidateService.validateFields(
                participant,
                new String[]{
                        "name",
                        "gitHubName"
                }, new String[]{
                        namePrefix,
                        gitHubNamePrefix
                }
        );

        if (participantRepository.findByCourseAndName(course, name) != null) {
            allErrors.add(String.format("%s %s", namePrefix, localeService.get("UniqueName")));
        }

        if (participantRepository.findByCourseAndGitHubName(course, gitHubName) != null) {
            allErrors.add(String.format("%s %s", gitHubNamePrefix, localeService.get("UniqueName")));
        }

        if (!allErrors.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return allErrors;
        }

        participantRepository.save(participant);
        return singletonList(localeService.get("m.participants.add.done"));
    }

    @RequestMapping(
            value = "/m/courses/participants/delete",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> delete(@ModelAttribute("id") Long2 participantID,
                               HttpServletResponse response) {

        if (!participantRepository.exists(participantID.get())) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.get("NoResource"));
        }

        participantRepository.delete(participantID.get());
        return singletonList(localeService.get("m.participants.delete.done"));
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

        if (!participantRepository.exists(participantID.get())) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.get("NoResource"));
        }

        Participant participant = participantRepository.getOne(participantID.get());

        if (participantRepository.findByCourseAndName(participant.getCourse(), newName) != null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.get("UniqueName"));
        }

        List<String> nameErrors = fieldValidateService.validateField(
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

        return singletonList(localeService.get("m.participants.rename.participant.name.done"));
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

        if (!participantRepository.exists(participantID.get())) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.get("NoResource"));
        }

        Participant participant = participantRepository.getOne(participantID.get());

        if (participantRepository.findByCourseAndGitHubName(participant.getCourse(), newGitHubName) != null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.get("UniqueName"));
        }

        List<String> gitHubNameErrors = fieldValidateService.validateField(
                new Participant(null, null, newGitHubName),
                "gitHubName",
                localeService.get("m.participants.add.validation.prefix.github.name")
        );

        if (!gitHubNameErrors.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return gitHubNameErrors;
        }

        participant.setGitHubName(newGitHubName);
        participantRepository.save(participant);

        return singletonList(localeService.get("m.participants.rename.github.name.done"));
    }
}
