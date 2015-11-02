package pl.p.lodz.iis.hr.services;

import org.springframework.stereotype.Service;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.models.forms.questions.Input;
import pl.p.lodz.iis.hr.models.forms.questions.InputScale;
import pl.p.lodz.iis.hr.models.forms.questions.InputText;
import pl.p.lodz.iis.hr.models.forms.questions.Question;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;

@Service
public class FormToXMLService {

    public Document getDocument(Form form) throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();

        Element formE = doc.createElement("form");
        doc.appendChild(formE);

        Element descriptionE = doc.createElement("description");
        CDATASection descriptionCDATA = doc.createCDATASection(form.getDescription());
        descriptionE.appendChild(descriptionCDATA);
        formE.appendChild(descriptionE);
//
//        for (Question question : form.getQuestions()) {
//            Element questionE = doc.createElement("question");
//
//            Element questionTextE = doc.createElement("questionText");
//            questionE.setTextContent(question.getQuestionText());
//            questionE.appendChild(questionTextE);
//
//            CDATASection additionalTipsE = doc.createCDATASection("additionalTips");
//            additionalTipsE.setTextContent(question.getAdditionalTips());
//            questionE.appendChild(additionalTipsE);
//
//            for (Input input : question.getInputs()) {
//                Element inputE = doc.createElement("inputE");
//                inputE.setAttribute("type", inputAsTextType(input));
//                appendInputChildren(doc, input, inputE);
//                questionE.appendChild(inputE);
//            }
//
//
//            formE.appendChild(questionE);
//        }

        return doc;
    }

    public String prettyPrint(Document xml) throws TransformerException, IOException {
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");

        try (StringWriter out = new StringWriter()) {
            tf.transform(new DOMSource(xml), new StreamResult(out));
            return out.toString();
        }
    }


    private void appendInputChildren(Document doc, Input input, Node inputE) {
        Element labelE = doc.createElement("label");
        labelE.setTextContent(input.getLabel());
        inputE.appendChild(labelE);

        Element requiredE = doc.createElement("required");
        requiredE.setTextContent(Boolean.toString(input.isRequired()));
        inputE.appendChild(requiredE);

        if (input instanceof InputScale) {
            InputScale inputScale = (InputScale) input;

            Element fromLabelE = doc.createElement("fromLabel");
            fromLabelE.setTextContent(inputScale.getFromLabel());
            inputE.appendChild(fromLabelE);

            Element fromE = doc.createElement("from");
            fromE.setTextContent(Long.toString(inputScale.getFromS()));
            inputE.appendChild(fromE);

            Element toLabelE = doc.createElement("toLabel");
            toLabelE.setTextContent(inputScale.getToLabel());
            inputE.appendChild(toLabelE);

            Element toE = doc.createElement("to");
            toE.setTextContent(Long.toString(inputScale.getToS()));
            inputE.appendChild(toE);
        }
    }

    private String inputAsTextType(Input input) {
        if (input instanceof InputText) {
            return "text";
        }
        if (input instanceof InputScale) {
            return "scale";
        }

        throw new AssertionError();
    }

}
