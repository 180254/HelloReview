package pl.p.lodz.iis.hr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.models.forms.questions.InputScale;
import pl.p.lodz.iis.hr.models.forms.questions.InputText;
import pl.p.lodz.iis.hr.models.forms.questions.Question;
import pl.p.lodz.iis.hr.repositories.FormRepository;
import pl.p.lodz.iis.hr.repositories.InputScaleRepository;
import pl.p.lodz.iis.hr.repositories.InputTextRepository;
import pl.p.lodz.iis.hr.repositories.QuestionRepository;

import java.util.List;

@Controller
public class RestController {

    //    @Autowired private FormQuestionsRepoService formService;
    @Autowired private FormRepository formRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private InputScaleRepository inputScaleRepository;
    @Autowired private InputTextRepository inputTextRepository;


//    @Autowired private FormItemScaleRepository formItemScaleRepository;
//    @Autowired private FormItemTextRepository formItemTextRepository;
//

//    @RequestMapping(value = "/master/rest/items",
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
    public List<Form> forms() {
        return formRepository.findAll();
    }

    @RequestMapping(value = "/rest/questions",
            method = RequestMethod.GET,
            produces = "application/json")
    @ResponseBody
    public List<Question> questions() {
        return questionRepository.findAll();
    }



    @RequestMapping(value = "/init",
            method = RequestMethod.GET,
            produces = "application/json")
    @ResponseBody
    public String init() {
        Form form = new Form();
        form.setName("Example");
        form = formRepository.saveAndFlush(form);

        Question question = new Question();
        question.setForm(form);
        question.setQuestionText("Czy poprawnie sformatowano kod.");
        question = questionRepository.saveAndFlush(question);

        InputScale inputScale = new InputScale();
        inputScale.setQuestion(question);
        inputScale.setLabel("Oceń w skali:");
        inputScale.setFromLabel("NIE");
        inputScale.setToLabel("TAK");
        inputScale.setFromS(0);
        inputScale.setToS(00);
        inputScale = inputScaleRepository.saveAndFlush(inputScale);

        InputText inputText = new InputText();
        inputText.setQuestion(question);
        inputText.setLabel("Dodatkowy komentarz:");
        inputText = inputTextRepository.saveAndFlush(inputText);
        return "";
    }
}
