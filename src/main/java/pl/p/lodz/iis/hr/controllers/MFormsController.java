package pl.p.lodz.iis.hr.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Strings;
import com.google.common.io.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.p.lodz.iis.hr.configuration.long2.Long2;
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

import static java.util.Collections.singletonList;

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
    public String list(Model model) {

        List<Form> byTemporaryFalse = formRepository.findByTemporaryFalse();
        model.addAttribute("forms", byTemporaryFalse);
        model.addAttribute("newButton", true);

        return "m-forms";
    }

    @RequestMapping(
            value = "/m/forms/{formID}",
            method = RequestMethod.GET)
    @Transactional
    public String listOne(@PathVariable Long2 formID,
                          Model model) {

        if (!formRepository.exists(formID.get())) {
            throw new ResourceNotFoundException();
        }

        Form form = formRepository.getOne(formID.get());

        if (form.isTemporary()) {
            throw new ResourceNotFoundException();
        }

        model.addAttribute("forms", singletonList(form));
        model.addAttribute("newButton", false);

        return "m-forms";
    }


    @RequestMapping(
            value = "/m/forms/add",
            method = RequestMethod.GET)
    public String kAdd() {
        return "m-forms-add";
    }

    @RequestMapping(
            value = "/m/forms/add",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> kAddPOST(@ModelAttribute("form-name") String formName,
                                 @ModelAttribute("form-xml") String formXML,
                                 @ModelAttribute("action") String action,
                                 HttpServletResponse response) {

        if (!Arrays.asList("preview", "add").contains(action)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return Collections.emptyList();
        }

        Form form;

        try {
            ObjectReader xmlReader = xmlMapperProvider.getXmlMapper()
                    .readerFor(Form.class).withView(JSONViews.FormParseXML.class);
            form = xmlReader.readValue(formXML.trim());

        } catch (IOException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(String.format("It's not valid form xml! Exception thrown: [%s]", e.getMessage()));
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

        return singletonList(String.valueOf(form.getId()));
    }

    @RequestMapping(
            value = "/m/forms/preview/{formID}",
            method = RequestMethod.GET)
    @Transactional
    public String preview(@PathVariable Long2 formID,
                          Model model) {

        if (!formRepository.exists(formID.get())) {
            throw new ResourceNotFoundException();
        }

        Form form = formRepository.getOne(formID.get());
        model.addAttribute("form", form);

        return "m-forms-preview";
    }

    @RequestMapping(
            value = "/m/forms/xml/example",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String xmlExample() throws IOException {
        URL resource = Resources.getResource("templates/m-forms-xml-example.xml");
        return Resources.toString(resource, StandardCharsets.UTF_8);
    }

    @RequestMapping(
            value = "/m/forms/xml/{formID}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_XML_VALUE)
    @Transactional
    @ResponseBody
    public String xml(@PathVariable Long2 formID) throws JsonProcessingException {

        if (!formRepository.exists(formID.get())) {
            throw new ResourceNotFoundException();
        }

        Form form = formRepository.getOne(formID.get());
        ObjectWriter objectWriter = xmlMapperProvider.getXmlMapper().writerWithView(JSONViews.FormParseXML.class);
        return objectWriter.writeValueAsString(form);
    }

    @RequestMapping(
            value = "/m/forms/delete",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> delete(@ModelAttribute("id") Long2 formID,
                               HttpServletResponse response) {

        if (!formRepository.exists(formID.get())) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.getMessage("NoResource"));
        }

        formRepository.delete(formID.get());
        return singletonList(localeService.getMessage("m.forms.delete.done"));
    }

    @RequestMapping(
            value = "/m/forms/rename",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> rename(@ModelAttribute("value") String newName,
                               @ModelAttribute("pk") Long2 formID,
                               HttpServletResponse response) {

        if (!formRepository.exists(formID.get())) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return singletonList(localeService.getMessage("NoResource"));
        }

        List<String> nameErrors = validateService.validateField(
                new Form(newName, null),
                "name",
                localeService.getMessage("m.forms.rename.validation.prefix.name")
        );

        if (!nameErrors.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return nameErrors;
        }

        Form form = formRepository.getOne(formID.get());
        form.setName(newName);
        formRepository.save(form);

        return singletonList(localeService.getMessage("m.forms.rename.done"));
    }
}
