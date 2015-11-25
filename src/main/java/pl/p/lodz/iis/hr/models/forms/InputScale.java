package pl.p.lodz.iis.hr.models.forms;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.MoreObjects;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import pl.p.lodz.iis.hr.models.JSONViews;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
//@PrimaryKeyJoinColumn(name = "id")
public class InputScale extends Input {

    private static final long serialVersionUID = -2230675781077172644L;

    @Column(nullable = false, length = 255)
    @JsonView(JSONViews.FormParseXML.class)
    private String fromLabel;

    @Column(nullable = false)
    @JsonView(JSONViews.FormParseXML.class)
    private Long fromS;

    @Column(nullable = false, length = 255)
    @JsonView(JSONViews.FormParseXML.class)
    private String toLabel;

    @Column(nullable = false)
    @JsonView(JSONViews.FormParseXML.class)
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

    @NotBlank
    @Length(min = 1, max = 255)
    public String getFromLabel() {
        return fromLabel;
    }

    /* package */ void setFromLabel(String fromLabel) {
        this.fromLabel = fromLabel;
    }

    @NotNull
    public Long getFromS() {
        return fromS;
    }

    /* package */ void setFromS(Long fromS) {
        this.fromS = fromS;
    }

    @NotBlank
    @Length(min = 1, max = 255)
    public String getToLabel() {
        return toLabel;
    }

    /* package */ void setToLabel(String toLabel) {
        this.toLabel = toLabel;
    }

    @NotNull
    public Long getToS() {
        return toS;
    }

    /* package */ void setToS(Long toS) {
        this.toS = toS;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fromLabel", fromLabel)
                .add("fromS", fromS)
                .add("toLabel", toLabel)
                .add("toS", toS)
                .toString();
    }
}
