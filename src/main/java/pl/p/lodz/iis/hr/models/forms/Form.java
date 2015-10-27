package pl.p.lodz.iis.hr.models.forms;

import pl.p.lodz.iis.hr.models.forms.questions.Question;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Form implements Serializable {

    private static final long serialVersionUID = -6520652024047473630L;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "form")
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<Question> questions = new ArrayList<>(10);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_id")
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, length = 5120)
    private String description;

    Form() {
    }

    public Form(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Question> getQuestions() {
        return new ArrayList<>(questions);
    }
}
