package pl.p.lodz.iis.hr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;
import pl.p.lodz.iis.hr.models.courses.Course;
import pl.p.lodz.iis.hr.models.courses.Participant;
import pl.p.lodz.iis.hr.repositories.CourseRepository;
import pl.p.lodz.iis.hr.repositories.ParticipantRepository;
import pl.p.lodz.iis.hr.services.LocaleService;
import pl.p.lodz.iis.hr.services.ValidateService;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

@Controller
class MCoursesParticipantsController {

    @Autowired private CourseRepository courseRepository;
    @Autowired private ParticipantRepository participantRepository;
    @Autowired private ValidateService validateService;
    @Autowired private LocaleService localeService;

    @RequestMapping(
            value = "/m/courses/{courseID}/participants",
            method = RequestMethod.GET)
    @Transactional
    public String list(@PathVariable long courseID,
                       Model model) {

        if (!courseRepository.exists(courseID)) {
            throw new ResourceNotFoundException();
        }

        Course course = courseRepository.getOne(courseID);

        model.addAttribute("newButton", true);
        model.addAttribute("course", course);
        model.addAttribute("participants", course.getParticipants());

        return "m-courses-participants";
    }

    @RequestMapping(
            value = "/m/courses/participants/{participantID}",
            method = RequestMethod.GET)
    @Transactional
    public String listOne(@PathVariable long participantID,
                          Model model) {

        if (!participantRepository.exists(participantID)) {
            throw new ResourceNotFoundException();
        }

        Participant participant = participantRepository.getOne(participantID);

        model.addAttribute("newButton", false);
        model.addAttribute("course", participant.getCourse());
        model.addAttribute("participants", singletonList(participant));

        return "m-courses-participants";
    }

    @RequestMapping(
            value = "/m/courses/participants/add",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> kAddPOST(@ModelAttribute("course-id") long courseID,
                                 @ModelAttribute("course-participant-name") String name,
                                 @ModelAttribute("course-participant-github-name") String gitHubName,
                                 HttpServletResponse response) {

        if (!courseRepository.exists(courseID)) {
            return singletonList(localeService.getMessage("NoResource"));
        }

        Course course = courseRepository.getOne(courseID);
        Participant participant = new Participant(course, name, gitHubName);

        String namePrefix = localeService.getMessage("m.courses.participants.add.validation.prefix.participant.name");
        String gitHubNamePrefix = localeService.getMessage("m.courses.participants.add.validation.prefix.github.name");

        List<String> nameErrors = validateService.validateField(participant, "name", namePrefix);
        List<String> gitHubNameErrors = validateService.validateField(participant, "gitHubName", gitHubNamePrefix);

        List<String> allErrors = new ArrayList<>(nameErrors.size() + gitHubNameErrors.size());
        allErrors.addAll(nameErrors);
        allErrors.addAll(gitHubNameErrors);

        if (participantRepository.findByCourseAndName(course, name) != null) {
            allErrors.add(String.format("%s %s", namePrefix, localeService.getMessage("UniqueName")));
        }

        if (participantRepository.findByCourseAndGitHubName(course, gitHubName) != null) {
            allErrors.add(String.format("%s %s", gitHubNamePrefix, localeService.getMessage("UniqueName")));
        }

        if (!allErrors.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return allErrors;
        }

        participantRepository.save(participant);
        return singletonList(localeService.getMessage("m.courses.participants.add.done"));
    }

    @RequestMapping(
            value = "/m/courses/participants/delete",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> delete(@ModelAttribute("id") long participantID,
                               HttpServletResponse response) {

        if (!participantRepository.exists(participantID)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.getMessage("NoResource"));
        }

        participantRepository.delete(participantID);
        return singletonList(localeService.getMessage("m.courses.participants.delete.done"));
    }

    @RequestMapping(
            value = "/m/courses/participants/rename/name",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> renameName(@ModelAttribute("value") String newName,
                                   @ModelAttribute("pk") long participantID,
                                   HttpServletResponse response) {

        if (!participantRepository.exists(participantID)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.getMessage("NoResource"));
        }

        Participant participant = participantRepository.getOne(participantID);

        if (participantRepository.findByCourseAndName(participant.getCourse(), newName) != null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.getMessage("UniqueName"));
        }

        Participant testParticipant = new Participant(null, newName, null);

        String namePrefix = localeService.getMessage("m.courses.participants.add.validation.prefix.participant.name");
        List<String> nameErrors = validateService.validateField(testParticipant, "name", namePrefix);

        if (!nameErrors.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return nameErrors;
        }

        participant.setName(newName);
        participantRepository.save(participant);

        return singletonList(localeService.getMessage("m.courses.participants.rename.participant.name.done"));
    }

    @RequestMapping(
            value = "/m/courses/participants/rename/githubname",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> renameGitHubName(@ModelAttribute("value") String newGitHubName,
                                         @ModelAttribute("pk") long participantID,
                                         HttpServletResponse response) {

        if (!participantRepository.exists(participantID)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.getMessage("NoResource"));
        }

        Participant participant = participantRepository.getOne(participantID);

        if (participantRepository.findByCourseAndGitHubName(participant.getCourse(), newGitHubName) != null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.getMessage("UniqueName"));
        }

        Participant testParticipant = new Participant(null, null, newGitHubName);

        String gitHubNamePrefix = localeService.getMessage("m.courses.participants.add.validation.prefix.github.name");
        List<String> gitHubNameErrors = validateService.validateField(testParticipant, "gitHubName", gitHubNamePrefix);

        if (!gitHubNameErrors.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return gitHubNameErrors;
        }

        participant.setGitHubName(newGitHubName);
        participantRepository.save(participant);

        return singletonList(localeService.getMessage("m.courses.participants.rename.github.name.done"));
    }
}
