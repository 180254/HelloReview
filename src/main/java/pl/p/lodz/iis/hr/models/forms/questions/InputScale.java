package pl.p.lodz.iis.hr.models.forms.questions;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import pl.p.lodz.iis.hr.models.forms.FormViews;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class InputScale extends Input {

    private static final long serialVersionUID = -2230675781077172644L;

    @Column(nullable = false, length = 255)
    @JsonView({FormViews.RESTPreview.class, FormViews.XMLTemplate.class})
    @Length(min = 1, max = 255)
    @NotBlank
    private String fromLabel;

    @Column(nullable = false)
    @JsonView({FormViews.RESTPreview.class, FormViews.XMLTemplate.class})
    private long fromS;

    @Column(nullable = false, length = 255)
    @JsonView({FormViews.RESTPreview.class, FormViews.XMLTemplate.class})
    @Length(min = 1, max = 255)
    @NotBlank
    private String toLabel;

    @Column(nullable = false)
    @JsonView({FormViews.RESTPreview.class, FormViews.XMLTemplate.class})
    private long toS;


    InputScale() {
    }

    public InputScale(Question question, String label,
                      String fromLabel, long fromS,
                      String toLabel, long toS) {
        super(question, label);
        this.fromLabel = fromLabel;
        this.fromS = fromS;
        this.toLabel = toLabel;
        this.toS = toS;
    }

    public String getFromLabel() {
        return fromLabel;
    }

    public long getFromS() {
        return fromS;
    }

    public String getToLabel() {
        return toLabel;
    }

    public long getToS() {
        return toS;
    }

}
