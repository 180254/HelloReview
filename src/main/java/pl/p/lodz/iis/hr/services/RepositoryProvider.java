package pl.p.lodz.iis.hr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.repositories.*;

@Service
public class RepositoryProvider {

    private final AnswerRepository answerRepository;
    private final CommissionRepository commissionRepository;
    private final CourseRepository courseRepository;
    private final FormRepository formRepository;
    private final InputRepository inputRepository;
    private final InputScaleRepository inputScaleRepository;
    private final InputTextRepository inputTextRepository;
    private final ParticipantRepository participantRepository;
    private final QuestionRepository questionRepository;
    private final ResponseRepository responseRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public RepositoryProvider(AnswerRepository answerRepository,
                              CommissionRepository commissionRepository,
                              CourseRepository courseRepository,
                              FormRepository formRepository,
                              InputRepository inputRepository,
                              InputScaleRepository inputScaleRepository,
                              InputTextRepository inputTextRepository,
                              ParticipantRepository participantRepository,
                              QuestionRepository questionRepository,
                              ResponseRepository responseRepository,
                              ReviewRepository reviewRepository) {
        this.answerRepository = answerRepository;
        this.commissionRepository = commissionRepository;
        this.courseRepository = courseRepository;
        this.formRepository = formRepository;
        this.inputRepository = inputRepository;
        this.inputScaleRepository = inputScaleRepository;
        this.inputTextRepository = inputTextRepository;
        this.participantRepository = participantRepository;
        this.questionRepository = questionRepository;
        this.responseRepository = responseRepository;
        this.reviewRepository = reviewRepository;
    }

    public AnswerRepository answer() {
        return answerRepository;
    }

    public CommissionRepository commission() {
        return commissionRepository;
    }

    public CourseRepository course() {
        return courseRepository;
    }

    public FormRepository form() {
        return formRepository;
    }

    public InputRepository input() {
        return inputRepository;
    }

    public InputScaleRepository inputScale() {
        return inputScaleRepository;
    }

    public InputTextRepository inputText() {
        return inputTextRepository;
    }

    public ParticipantRepository participant() {
        return participantRepository;
    }

    public QuestionRepository question() {
        return questionRepository;
    }

    public ResponseRepository response() {
        return responseRepository;
    }

    public ReviewRepository review() {
        return reviewRepository;
    }
}
