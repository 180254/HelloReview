package pl.p.lodz.iis.hr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.models.forms.questions.Input;

import java.util.List;

@Controller
public class MasterFormsController {

//    @Autowired private FormRepository formRepository;
//    @Autowired private FormQuestionsRepoService formQuestionsRepoService;
//
//    @RequestMapping(
//            value = "/master/forms/preview/{formID}",
//            method = RequestMethod.GET)
//    public String index(
//            @PathVariable long formID,
//            Model model) {
//
//        Form form = formRepository.getOne(formID);
//        List<? super Input> formItems = formQuestionsRepoService.getFormItemsForForm(form);
//
//        model.addAttribute("form", form);
//        model.addAttribute("formItems", formItems);
//        return "master-forms-preview";
//    }
}
