package pl.p.lodz.iis.hr.controllers;

import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    public String pList(@PathVariable long courseID,
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
    public String pListOne(@PathVariable long participantID,
                           Model model) {

        if (!participantRepository.exists(participantID)) {
            throw new ResourceNotFoundException();

        }

        Participant participant = participantRepository.getOne(participantID);

        model.addAttribute("newButton", false);
        model.addAttribute("course", participant.getCourse());
        model.addAttribute("participants", Collections.singletonList(participant));

        return "m-courses-participants";
    }

    @RequestMapping(
            value = "/m/courses/participants/add",
            method = RequestMethod.POST)
    @Transactional
    @ResponseBody
    public Object pAddPOST(@NonNls @ModelAttribute("course-id") long courseID,
                           @NonNls @ModelAttribute("course-participant-name") String name,
                           @NonNls @ModelAttribute("course-participant-github-name") String gitHubName,
                           HttpServletResponse response) throws IOException {

        if (!courseRepository.exists(courseID)) {
            return localeService.getMessage("NoResource");
        }

        Course course = courseRepository.getOne(courseID);
        Participant participant = new Participant(course, name, gitHubName);

        String namePrefix = localeService.getMessage("m.courses.participants.add.validation.prefix.participant.name");
        String gitHubNamePrefix = localeService.getMessage("m.courses.participants.add.validation.prefix.github.name");

        List<String> nameErrors = validateService.validateField(participant, "name", namePrefix);
        List<String> gitHubNameErrors = validateService.validateField(participant, "gitHubName", gitHubNamePrefix);

        Collection<String> allErrors = new ArrayList<>(nameErrors.size() + gitHubNameErrors.size());
        allErrors.addAll(nameErrors);
        allErrors.addAll(gitHubNameErrors);

        if (!allErrors.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return allErrors;
        }

        participantRepository.save(participant);
        return localeService.getMessage("m.courses.participants.add.done");
    }

    @RequestMapping(
            value = "/m/courses/participants/delete/{participantID}",
            method = RequestMethod.POST)
    @Transactional
    @ResponseBody
    public String pDelete(@PathVariable long participantID,
                          HttpServletResponse response) {

        if (!participantRepository.exists(participantID)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return localeService.getMessage("NoResource");
        }

        participantRepository.delete(participantID);
        return localeService.getMessage("m.courses.participants.delete.done");
    }
}
