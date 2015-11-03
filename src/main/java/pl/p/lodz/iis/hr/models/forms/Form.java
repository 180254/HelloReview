package pl.p.lodz.iis.hr.models.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import pl.p.lodz.iis.hr.models.forms.questions.Question;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@JacksonXmlRootElement(localName = "form")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Form implements Serializable {

    private static final long serialVersionUID = -6520652024047473630L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_id")
    @JsonView(FormViews.RESTPreview.class)
    private long id;

    @Column(nullable = false, length = 255)
    @JsonView(FormViews.RESTPreview.class)
    @Length(min = 1, max = 255)
    @NotBlank
    private String name;

    @Column(nullable = false, length = 4095)
    @JsonView({FormViews.RESTPreview.class, FormViews.XMLTemplate.class})
    @Length(min = 1, max = 4095)
    @NotBlank
    private String description;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "form")
    @JsonView({FormViews.RESTPreview.class, FormViews.XMLTemplate.class})
    @JsonProperty("question")
    private final List<Question> questions = new ArrayList<>(10);

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
