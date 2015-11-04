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
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.models.forms.FormViews;
import pl.p.lodz.iis.hr.repositories.FormRepository;
import pl.p.lodz.iis.hr.services.FormValidator;
import pl.p.lodz.iis.hr.services.XmlMapperProvider;
import pl.p.lodz.iis.hr.utils.ExceptionChecker;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Controller
public class MasterFormsController {

    @Autowired private FormRepository formRepository;
    @Autowired private XmlMapperProvider xmlMapperProvider;
    @Autowired private FormValidator formValidator;

    @RequestMapping(
            value = "/m/forms",
            method = RequestMethod.GET)
    public String fList(Model model) {
        List<Form> byTemporaryFalse = formRepository.findByTemporaryFalse();
        model.addAttribute("forms", byTemporaryFalse);
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
    @ResponseBody
    @Transactional
    public Object fAddPOST(
            @NonNls @ModelAttribute("form-template-name") String formName,
            @NonNls @ModelAttribute("form-xml") String formXML,
            @NonNls @ModelAttribute("action") String action,
            HttpServletResponse response) throws IOException {

        boolean badAction = (action == null)
                || (!action.equals("preview") && !action.equals("add"));

        if (badAction) {
            response.sendError(HttpStatus.BAD_REQUEST.value());
            return StringUtils.EMPTY;
        }

        ObjectReader xmlReader = xmlMapperProvider.getXmlMapper()
                .readerFor(Form.class).withView(FormViews.XMLTemplate.class);

        try {
            Form form = xmlReader.readValue(formXML);
            form.setTemplateName(Strings.emptyToNull(formName));
            form.setTemporary(action.equals("preview"));

            List<String> validate = formValidator.validate(form);
            if (!validate.isEmpty()) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return validate;
            }

            form.fixRelations();
            formRepository.delete(formRepository.findByTemporaryTrue());
            formRepository.save(form);
            return form.getId();

        } catch (IOException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return Collections.singletonList(String.format("it's not xml > exception thrown: [%s]", e.getMessage()));
        } catch (RuntimeException e) {
            return Collections.singletonList("internal server error occurred");

        }

    }


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
            value = "/m/forms/xml/example",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_XML_VALUE)
    @ResponseBody
    public String xmlExample()
            throws IOException {
        URL resource = Resources.getResource("templates/m-forms-xml-example.xml");
        return Resources.toString(resource, StandardCharsets.UTF_8);
    }

    @RequestMapping(
            value = "/m/forms/xml/{formID}",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_XML_VALUE)
    @ResponseBody
    public String xml(@PathVariable long formID) throws JsonProcessingException {

        Form form = formRepository.getOne(formID);

        if (ExceptionChecker.checkExceptionThrown(form::getId)) {
            throw new ResourceNotFoundException();
        }

//        return new ObjectMapper().writerWithView(FormViews.XMLTemplate.class).writeValueAsString(form);
        ObjectWriter objectWriter = xmlMapperProvider.getXmlMapper().writerWithView(FormViews.XMLTemplate.class);
        return objectWriter.writeValueAsString(form);
    }
}
