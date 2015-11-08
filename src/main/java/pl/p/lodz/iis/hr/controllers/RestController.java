package pl.p.lodz.iis.hr.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.p.lodz.iis.hr.models.JSONViews;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.models.forms.InputScale;
import pl.p.lodz.iis.hr.models.forms.InputText;
import pl.p.lodz.iis.hr.models.forms.Question;
import pl.p.lodz.iis.hr.repositories.FormRepository;
import pl.p.lodz.iis.hr.repositories.InputScaleRepository;
import pl.p.lodz.iis.hr.repositories.InputTextRepository;
import pl.p.lodz.iis.hr.repositories.QuestionRepository;

@Controller
public class RestController {

    //    @Autowired private FormQuestionsRepoService formService;
    @Autowired private FormRepository formRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private InputScaleRepository inputScaleRepository;
    @Autowired private InputTextRepository inputTextRepository;
    @Autowired private ObjectMapper objectMapper;


//    @Autowired private FormItemScaleRepository formItemScaleRepository;
//    @Autowired private FormItemTextRepository formItemTextRepository;
//

//    @RequestMapping(value = "/m/rest/items",
//            method = RequestMethod.GET,
//            produces = "application/json")
//    @ResponseBody
//    public List<? super Input> formItems() {
//        List<? super Input> formItems = new ArrayList<>(10);
//        formItems.addAll(formItemScaleRepository.findAll());
//        formItems.addAll(formItemTextRepository.findAll());
//        return formItems;
//}

    @RequestMapping(value = "/rest/forms",
            method = RequestMethod.GET,
            produces = "application/json")
    @ResponseBody
    public String forms() throws JsonProcessingException {
        ObjectWriter objectWriter = objectMapper.writerWithView(JSONViews.FormRESTPreview.class);
        return objectWriter.writeValueAsString(formRepository.findAll());
    }

    @RequestMapping(value = "/init",
            method = RequestMethod.GET,
            produces = "application/json")
    @ResponseBody
    public String init() {
        Form form = new Form("Example", "Proszę ocenić {projectname}. <b>Hehe</b><br/>Deadline jest {deadline}.");
        form = formRepository.saveAndFlush(form);

        Question question = new Question(form, "Czy poprawnie sformatowano kod?", null);
        question = questionRepository.saveAndFlush(question);


        InputScale inputScale = new InputScale(
                question, "Oceń w skali",
                "TAK", 0L, "NIE", 10L);
        inputScale = inputScaleRepository.saveAndFlush(inputScale);


        InputText inputText = new InputText(question, "Dodatkowy komentarz:");
        inputText = inputTextRepository.saveAndFlush(inputText);

        //


        question = new Question(form, "Ala ma kota",
                "Nunc ullamcorper odio non <b>leo bibendum, vitae aliquet lectus varius." +
                        "Praesent viverra leo tellus, in eleifend enim elementum eget. Nunc ac euismod elit." +
                        "In ornare quam nisi, sed elementum risus dapibus quis</b>. Vestibulum vel quam condimentum," +
                        "elementum risus et, euismod arcu. Suspendisse egestas, mauris sed scelerisque blandit," +
                        "libero quam sagittis justo, eget efficitur leo justo non eros. Phasellus massa quam, mattis" +
                        "non est in, volutpat auctor lacus.\n" +
                        "Etiam risus risus, dignissim quis enim eget, placerat sagittis leo. Etiam egestas rutrum magna" +
                        "vitae tempus. Nullam at consectetur mauris. Suspendisse fringilla, nibh rhoncus dictum faucibus," +
                        "lectus neque mattis libero, a scelerisque turpis. ");
        question = questionRepository.saveAndFlush(question);

        inputText = new InputText(question, "Coś napisz:");
        inputText = inputTextRepository.saveAndFlush(inputText);

        inputScale = new InputScale(
                question, "Oceń w skali",
                "Super", 0L, "Beznadziejnie", 10L);
        inputScale = inputScaleRepository.saveAndFlush(inputScale);

        //

        question = new Question(form, "Czy poprawnie użyto banana?", null);
        question = questionRepository.saveAndFlush(question);

        inputText = new InputText(question, "Dodatkowy komentarz:");
        inputText = inputTextRepository.saveAndFlush(inputText);

        inputScale = new InputScale(
                question, "Oceń w skali",
                "TAK", 0L, "NIE", 10L);
        inputScale = inputScaleRepository.saveAndFlush(inputScale);


        return "";
    }
}
