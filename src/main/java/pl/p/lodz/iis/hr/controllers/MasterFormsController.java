package pl.p.lodz.iis.hr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.repositories.FormRepository;
import pl.p.lodz.iis.hr.utils.ExceptionChecker;

@Controller
public class MasterFormsController {

    @Autowired private FormRepository formRepository;

    @RequestMapping(
            value = "/m/forms/preview/{formID}",
            method = RequestMethod.GET)
    public String preview(
            @PathVariable long formID,
            Model model) {

        Form form = formRepository.getOne(formID);

        if (ExceptionChecker.checkExceptionThrown(form::getId)) {
            throw new ResourceNotFoundException();
        }

        model.addAttribute("form", form);
        return "m-forms-preview";
    }

    @RequestMapping(
            value = "/m/forms/add",
            method = RequestMethod.GET)
    public String addF() {
        return "m-forms-add";
    }

    @RequestMapping(
            value = "/m/forms/example",
            method = RequestMethod.GET)
    public String example() {
        return "m-forms-example";
    }
}
