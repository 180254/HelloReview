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
import pl.p.lodz.iis.hr.exceptions.ErrorPageException;
import pl.p.lodz.iis.hr.exceptions.LocalizableErrorRestException;
import pl.p.lodz.iis.hr.exceptions.LocalizedErrorRestException;
import pl.p.lodz.iis.hr.models.JSONViews;
import pl.p.lodz.iis.hr.models.courses.Commission;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.models.forms.InputScale;
import pl.p.lodz.iis.hr.services.*;
import pl.p.lodz.iis.hr.utils.ProxyUtils;

import javax.servlet.http.HttpServletResponse;
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
    private final RepositoryProvider repositoryProvider;
    private final FormValidator formValidator;
    private final FieldValidator fieldValidator;
    private final XmlMapperProvider xmlMapperProvider;
    private final ReviewService reviewService;
    private final LocaleService localeService;

    @Autowired
    MFormsController(ResCommonService resCommonService,
                     RepositoryProvider repositoryProvider,
                     FormValidator formValidator,
                     FieldValidator fieldValidator,
                     XmlMapperProvider xmlMapperProvider,
                     ReviewService reviewService,
                     LocaleService localeService) {
        this.resCommonService = resCommonService;
        this.repositoryProvider = repositoryProvider;
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

        List<Form> byTemporaryFalse = repositoryProvider.form().findByTemporaryFalse();

        model.addAttribute("forms", byTemporaryFalse);
        model.addAttribute("newButton", true);

        return "m-forms";
    }

    @RequestMapping(
            value = "/m/forms/{formID}",
            method = RequestMethod.GET)
    @Transactional
    public String listOne(@PathVariable Long2 formID,
                          Model model) throws ErrorPageException {

        Form form = resCommonService.getOne(repositoryProvider.form(), formID.get());

        if (form.isTemporary()) {
            throw new ErrorPageException(HttpServletResponse.SC_NOT_FOUND);
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
            throws LocalizedErrorRestException, LocalizableErrorRestException, ErrorPageException {

        if (!Arrays.asList("preview", "add").contains(action)) {
            throw new ErrorPageException(HttpServletResponse.SC_BAD_REQUEST);
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

        List<Form> formRoBeDeleted = repositoryProvider.form().findByTemporaryTrue();
        repositoryProvider.form().delete(formRoBeDeleted);
        repositoryProvider.form().flush();

        form.fixRelations();
        formValidator.validateRestEx(form);

        // input scale must be required
        form.getQuestions().stream()
                .flatMap(q -> q.getInputs().stream())
                .filter(input -> ProxyUtils.isInstanceOf(input, InputScale.class))
                .forEach(input -> input.setRequired(true));

        LOGGER.debug("Form added {}", form);
        repositoryProvider.form().save(form);
        LOGGER.info("Added form {}", form);

        return Collections.singletonList(String.valueOf(form.getId()));
    }

    @RequestMapping(
            value = "/m/forms/preview/{formID}",
            method = RequestMethod.GET)
    @Transactional
    public String preview(@PathVariable Long2 formID,
                          Model model)
            throws ErrorPageException {

        Form form = resCommonService.getOne(repositoryProvider.form(), formID.get());

        Review review = new Review(null, 0L, null, form, "user/repo1_1");
        Commission commission = new Commission(review, null, null, (String) null);
        commission.setGhUrl("https://github.com/user1/84cc4672-c4fd-46dd-ab8a-378323cfce19");

        AnswerProvider answerProvider = new AnswerProvider(null);

        model.addAttribute("form", form);
        model.addAttribute("review", review);
        model.addAttribute("commission", commission);
        model.addAttribute("answerProvider", answerProvider);

        return "p-form";
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
            throws JsonProcessingException, ErrorPageException {

        Form form = resCommonService.getOne(repositoryProvider.form(), formID.get());
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
            throws LocalizableErrorRestException {

        Form form = resCommonService.getOneForRest(repositoryProvider.form(), formID.get());

        List<Review> reviews = form.getReviews();
        if (!reviewService.canBeDeleted(reviews)) {
            throw new LocalizableErrorRestException("m.reviews.delete.cannot.as.comm.processing");
        }

        LOGGER.info("Form deleted {}", form);
        reviewService.delete(reviews);
        repositoryProvider.form().delete(form);

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
            throws LocalizedErrorRestException, LocalizableErrorRestException {

        Form form = resCommonService.getOneForRest(repositoryProvider.form(), formID.get());

        fieldValidator.validateFieldRestEx(
                new Form(newName, null),
                "name",
                localeService.get("m.forms.rename.validation.prefix.name")
        );

        LOGGER.info("Form {} renamed to {}", form, newName);
        form.setName(newName);
        repositoryProvider.form().save(form);

        return localeService.getAsList("m.forms.rename.done");
    }
}
