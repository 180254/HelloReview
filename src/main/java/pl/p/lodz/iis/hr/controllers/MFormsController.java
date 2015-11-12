package pl.p.lodz.iis.hr.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Strings;
import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;
import pl.p.lodz.iis.hr.models.JSONViews;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.repositories.FormRepository;
import pl.p.lodz.iis.hr.services.FormValidator;
import pl.p.lodz.iis.hr.services.LocaleService;
import pl.p.lodz.iis.hr.services.ValidateService;
import pl.p.lodz.iis.hr.services.XmlMapperProvider;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
class MFormsController {

    @Autowired private FormRepository formRepository;
    @Autowired private XmlMapperProvider xmlMapperProvider;
    @Autowired private FormValidator formValidator;
    @Autowired private LocaleService localeService;
    @Autowired private ValidateService validateService;

    @RequestMapping(
            value = "/m/forms",
            method = RequestMethod.GET)
    @Transactional
    public String fList(Model model) {

        List<Form> byTemporaryFalse = formRepository.findByTemporaryFalse();
        model.addAttribute("forms", byTemporaryFalse);
        model.addAttribute("newButton", true);

        return "m-forms";
    }

    @RequestMapping(
            value = "/m/forms/{formID}",
            method = RequestMethod.GET)
    @Transactional
    public String fListOne(@PathVariable long formID,
                           Model model) {

        if (!formRepository.exists(formID)) {
            throw new ResourceNotFoundException();
        }

        Form form = formRepository.getOne(formID);

        if (form.isTemporary()) {
            throw new ResourceNotFoundException();
        }

        model.addAttribute("forms", Collections.singletonList(form));
        model.addAttribute("newButton", false);

        return "m-forms";
    }


    @RequestMapping(
            value = "/m/forms/add",
            method = RequestMethod.GET)
    public String fAddGET() {
        return "m-forms-add";
    }

    @RequestMapping(
            value = "/m/forms/add",
            method = RequestMethod.POST)
    @Transactional
    @ResponseBody
    public Object fAddPOST(@NonNls @ModelAttribute("form-name") String formName,
                           @NonNls @ModelAttribute("form-xml") String formXML,
                           @NonNls @ModelAttribute("action") String action,
                           HttpServletResponse response) throws IOException {

        if (!Arrays.asList("preview", "add").contains(action)) {
            response.sendError(HttpStatus.BAD_REQUEST.value());
            return StringUtils.EMPTY;
        }

        Form form;

        try {
            ObjectReader xmlReader = xmlMapperProvider.getXmlMapper()
                    .readerFor(Form.class).withView(JSONViews.FormParseXML.class);
            form = xmlReader.readValue(formXML.trim());

        } catch (IOException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return Collections.singletonList(
                    String.format("It's not valid form xml! Exception thrown: [%s]", e.getMessage()));
        }

        form.setName(Strings.emptyToNull(formName));
        form.setTemporary(action.equals("preview"));

        formRepository.delete(formRepository.findByTemporaryTrue());
        formRepository.flush();

        List<String> validate = formValidator.validate(form);
        if (!validate.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return validate;
        }

        form.fixRelations();
        formRepository.save(form);
        return form.getId();
    }

    @RequestMapping(
            value = "/m/forms/preview/{formID}",
            method = RequestMethod.GET)
    @Transactional
    public String preview(@PathVariable long formID,
                          Model model) {

        if (!formRepository.exists(formID)) {
            throw new ResourceNotFoundException();
        }

        Form form = formRepository.getOne(formID);
        model.addAttribute("form", form);

        return "m-forms-preview";
    }

    @RequestMapping(
            value = "/m/forms/xml/example",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_XML_VALUE)
    @ResponseBody
    public String xmlExample() throws IOException {
        URL resource = Resources.getResource("templates/m-forms-xml-example.xml");
        return Resources.toString(resource, StandardCharsets.UTF_8);
    }

    @RequestMapping(
            value = "/m/forms/xml/{formID}",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_XML_VALUE)
    @Transactional
    @ResponseBody
    public String xml(@PathVariable long formID) throws JsonProcessingException {

        if (!formRepository.exists(formID)) {
            throw new ResourceNotFoundException();
        }

        Form form = formRepository.getOne(formID);
        ObjectWriter objectWriter = xmlMapperProvider.getXmlMapper().writerWithView(JSONViews.FormParseXML.class);
        return objectWriter.writeValueAsString(form);
    }

    @RequestMapping(
            value = "/m/forms/delete/{formID}",
            method = RequestMethod.POST)
    @Transactional
    @ResponseBody
    public String delete(@PathVariable long formID,
                         HttpServletResponse response) {

        if (!formRepository.exists(formID)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return localeService.getMessage("NoResource");
        }

        formRepository.delete(formID);
        return localeService.getMessage("m.forms.delete.done");
    }

    @RequestMapping(
            value = "/m/forms/rename",
            method = RequestMethod.POST)
    @Transactional
    @ResponseBody
    public Object rename(@NonNls @ModelAttribute("value") String newName,
                         @NonNls @ModelAttribute("pk") long formID,
                         HttpServletResponse response) {

        if (formRepository.exists(formID)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return localeService.getMessage("NoResource");
        }

        Form testForm = new Form(newName, null);
        List<String> nameErrors = validateService.validateField(testForm, "name");
        if (!nameErrors.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return nameErrors;
        }

        Form form = formRepository.getOne(formID);
        form.setName(newName);
        formRepository.save(form);

        return localeService.getMessage("m.form.rename.done");
    }
}
