package pl.p.lodz.iis.hr.models.forms;

import pl.p.lodz.iis.hr.models.forms.questions.Question;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Form {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id")
    private List<Question> questions = new ArrayList<>(10);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
