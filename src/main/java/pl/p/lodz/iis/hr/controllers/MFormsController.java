package pl.p.lodz.iis.hr.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Strings;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.p.lodz.iis.hr.configuration.Long2;
import pl.p.lodz.iis.hr.exceptions.LocalizableErrorRestException;
import pl.p.lodz.iis.hr.exceptions.LocalizedErrorRestException;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;
import pl.p.lodz.iis.hr.models.JSONViews;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.repositories.FormRepository;
import pl.p.lodz.iis.hr.services.*;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
class MFormsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MFormsController.class);

    private final ResCommonService resCommonService;
    private final FormRepository formRepository;
    private final ReviewService reviewService;
    private final FormValidator formValidator;
    private final LocaleService localeService;
    private final FieldValidator fieldValidator;
    private final XmlMapperProvider xmlMapperProvider;

    @Autowired
    MFormsController(ResCommonService resCommonService,
                     FormRepository formRepository,
                     ReviewService reviewService,
                     FormValidator formValidator,
                     LocaleService localeService,
                     FieldValidator fieldValidator,
                     XmlMapperProvider xmlMapperProvider) {
        this.resCommonService = resCommonService;
        this.formRepository = formRepository;
        this.reviewService = reviewService;
        this.formValidator = formValidator;
        this.localeService = localeService;
        this.fieldValidator = fieldValidator;
        this.xmlMapperProvider = xmlMapperProvider;
    }

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
                          Model model)
            throws ResourceNotFoundException {

        Form form = resCommonService.getOne(formRepository, formID.get());

        if (form.isTemporary()) {
            throw new ResourceNotFoundException();
        }

        model.addAttribute("forms", Collections.singletonList(form));
        model.addAttribute("newButton", false);
        model.addAttribute("addon_oneForm", true);

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
                                 @ModelAttribute("action") String action)
            throws LocalizedErrorRestException, LocalizableErrorRestException {

        if (!Arrays.asList("preview", "add").contains(action)) {
            throw new LocalizableErrorRestException("");
        }

        Form form;

        try {
            ObjectReader xmlReader = xmlMapperProvider
                    .getXmlMapper()
                    .readerFor(Form.class)
                    .withView(JSONViews.FormParseXML.class);

            form = xmlReader.readValue(formXML.trim());

        } catch (IOException e) {
            throw (LocalizableErrorRestException)
                    new LocalizableErrorRestException("m.forms.add.validation.not.xml", e.getMessage()).initCause(e);
        }

        form.setName(Strings.emptyToNull(formName));
        form.setTemporary(action.equals("preview"));

        formRepository.delete(formRepository.findByTemporaryTrue());
        formRepository.flush();

        formValidator.validateRestEx(form);
        form.fixRelations();

        LOGGER.debug("Form added {}", form);
        formRepository.save(form);

        return Collections.singletonList(String.valueOf(form.getId()));
    }

    @RequestMapping(
            value = "/m/forms/preview/{formID}",
            method = RequestMethod.GET)
    @Transactional
    public String preview(@PathVariable Long2 formID,
                          Model model)
            throws ResourceNotFoundException {

        Form form = resCommonService.getOne(formRepository, formID.get());

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
    public String xml(@PathVariable Long2 formID)
            throws JsonProcessingException, ResourceNotFoundException {

        Form form = resCommonService.getOne(formRepository, formID.get());
        ObjectWriter objectWriter = xmlMapperProvider.getXmlMapper().writerWithView(JSONViews.FormParseXML.class);
        return objectWriter.writeValueAsString(form);
    }

    @RequestMapping(
            value = "/m/forms/delete",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> delete(@ModelAttribute("id") Long2 formID)
            throws LocalizableErrorRestException, ResourceNotFoundException {

        Form form = resCommonService.getOne(formRepository, formID.get());

        List<Review> reviews = form.getReviews();
        if (!reviewService.canBeDeleted(reviews)) {
            throw new LocalizableErrorRestException("m.reviews.delete.cannot.as.comm.processing");
        }

        LOGGER.debug("Form deleted {}", form);
        reviewService.delete(reviews);
        formRepository.delete(form);

        return localeService.getAsList("m.forms.delete.done");
    }

    @RequestMapping(
            value = "/m/forms/rename",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> rename(@ModelAttribute("value") String newName,
                               @ModelAttribute("pk") Long2 formID)
            throws ResourceNotFoundException, LocalizedErrorRestException {

        Form form = resCommonService.getOne(formRepository, formID.get());

        fieldValidator.validateFieldRestEx(
                new Form(newName, null),
                "name",
                localeService.get("m.forms.rename.validation.prefix.name")
        );

        LOGGER.debug("Form {} renamed to {}", form, newName);
        form.setName(newName);
        formRepository.save(form);

        return localeService.getAsList("m.forms.rename.done");
    }
}
