package pl.p.lodz.iis.hr.controllers;

import com.google.common.io.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.repositories.FormRepository;
import pl.p.lodz.iis.hr.services.FormToXMLService;
import pl.p.lodz.iis.hr.utils.ExceptionChecker;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Controller
public class MasterFormsController {

    @Autowired private FormRepository formRepository;
    @Autowired private FormToXMLService formToXMLService;

    @RequestMapping(
            value = "/m/forms/add",
            method = RequestMethod.GET)
    public String add() {
        return "m-forms-add";
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
            produces = "text/xml")
    @ResponseBody
    public String xmlExample()
            throws IOException {
        URL resource = Resources.getResource("templates/m-forms-xml-example.xml");
        return Resources.toString(resource, StandardCharsets.UTF_8);
    }

    @RequestMapping(
            value = "/m/forms/xml/{formID}",
            method = RequestMethod.GET,
            produces = "text/xml")
    @ResponseBody
    public String xml(@PathVariable long formID)
            throws ParserConfigurationException, TransformerException, IOException {

        Form form = formRepository.getOne(formID);

        if (ExceptionChecker.checkExceptionThrown(form::getId)) {
            throw new ResourceNotFoundException();
        }

        Document formDoc = formToXMLService.getDocument(form);
        return formToXMLService.prettyPrint(formDoc);

    }
}
