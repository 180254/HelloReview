package pl.p.lodz.iis.hr.controllers;

import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;
import pl.p.lodz.iis.hr.models.courses.Course;
import pl.p.lodz.iis.hr.repositories.CourseRepository;
import pl.p.lodz.iis.hr.services.LocaleService;
import pl.p.lodz.iis.hr.utils.ExceptionChecker;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
class MCoursesController {

    @Autowired private Validator validator;
    @Autowired private CourseRepository courseRepository;
    @Autowired private LocaleService localeService;

    @RequestMapping(
            value = "/m/courses",
            method = RequestMethod.GET)
    public String cList(Model model) {
        List<Course> courses = courseRepository.findAll();
        model.addAttribute("courses", courses);
        return "m-courses";
    }

    @RequestMapping(
            value = "/m/courses/{courseID}/participants",
            method = RequestMethod.GET)
    public String participants(@PathVariable long courseID,
                               Model model) {

        Course curse = courseRepository.getOne(courseID);

        if (ExceptionChecker.checkExceptionThrown(curse::getId)) {
            throw new ResourceNotFoundException();
        }

        model.addAttribute("curse", curse);
        return "m-courses-participants";
    }

    @RequestMapping(
            value = "/m/courses/add",
            method = RequestMethod.POST)
    @Transactional
    @ResponseBody
    public Object fCoursesAddPOST(@NonNls @ModelAttribute("course-name") String courseName,
                                  HttpServletResponse response) throws IOException {

        Course course = new Course(courseName);

        BindingResult bindingResult = new DataBinder(course).getBindingResult();
        validator.validate(course, bindingResult);

        if (bindingResult.hasErrors()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());

            List<FieldError> fieldErrors = bindingResult.getFieldErrors("name");
            return fieldErrors.stream().map(
                    fieldError -> localeService.getMessage(fieldError))
                    .sorted()
                    .collect(Collectors.toList());

        } else {
            courseRepository.save(course);
            return localeService.getMessage("m.courses.add.done");
        }
    }


    @RequestMapping(
            value = "/m/courses/delete/{courseID}",
            method = RequestMethod.POST)
    @Transactional
    @ResponseBody
    public String delete(@PathVariable long courseID,
                         HttpServletResponse response) {

        Course course = courseRepository.getOne(courseID);

        if (ExceptionChecker.checkExceptionThrown(course::getId)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return localeService.getMessage("NoResource");
        }

        courseRepository.delete(course);
        return localeService.getMessage("m.courses.delete.done");
    }

    @RequestMapping(
            value = "/m/courses/rename",
            method = RequestMethod.POST)
    @Transactional
    @ResponseBody
    public Object rename(@NonNls @ModelAttribute("value") String newName,
                         @NonNls @ModelAttribute("pk") long courseID,
                         HttpServletResponse response) {

        Course course = courseRepository.getOne(courseID);

        if (ExceptionChecker.checkExceptionThrown(course::getId)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return localeService.getMessage("NoResource");
        }

        if (courseRepository.findByName(newName) != null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return localeService.getMessage("UniqueName");
        }

        courseRepository.save(course);
        return localeService.getMessage("m.courses.rename.done");
    }

}
