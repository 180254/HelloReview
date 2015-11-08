package pl.p.lodz.iis.hr.models.forms;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import pl.p.lodz.iis.hr.models.JSONViews;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class InputScale extends Input {

    private static final long serialVersionUID = -2230675781077172644L;

    @Column(nullable = false, length = 255)
    @NotBlank
    @Length(min = 1, max = 255)
    @JsonView({JSONViews.FormRESTPreview.class, JSONViews.FormParseXML.class})
    private String fromLabel;

    @Column(nullable = false)
    @NotNull
    @JsonView({JSONViews.FormRESTPreview.class, JSONViews.FormParseXML.class})
    private Long fromS;

    @Column(nullable = false, length = 255)
    @NotBlank
    @Length(min = 1, max = 255)
    @JsonView({JSONViews.FormRESTPreview.class, JSONViews.FormParseXML.class})
    private String toLabel;

    @Column(nullable = false)
    @NotNull
    @JsonView({JSONViews.FormRESTPreview.class, JSONViews.FormParseXML.class})
    private Long toS;

    InputScale() {
    }

    public InputScale(Question question, String label,
                      String fromLabel, Long fromS,
                      String toLabel, Long toS) {
        super(question, label);
        this.fromLabel = fromLabel;
        this.fromS = fromS;
        this.toLabel = toLabel;
        this.toS = toS;
    }

    public String getFromLabel() {
        return fromLabel;
    }

    public Long getFromS() {
        return fromS;
    }

    public String getToLabel() {
        return toLabel;
    }

    public Long getToS() {
        return toS;
    }

}
