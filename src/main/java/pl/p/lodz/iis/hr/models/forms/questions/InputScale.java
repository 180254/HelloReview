package pl.p.lodz.iis.hr.models.forms.questions;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class InputScale extends Input {

    @Column(nullable = false)
    private long fromS;

    @Column(nullable = false)
    private long toS;

    @Column(nullable = false)
    private String fromLabel;

    @Column(nullable = false)
    private String toLabel;

    public long getFromS() {
        return fromS;
    }

    public void setFromS(long fromS) {
        this.fromS = fromS;
    }

    public long getToS() {
        return toS;
    }

    public void setToS(long toS) {
        this.toS = toS;
    }

    public String getFromLabel() {
        return fromLabel;
    }

    public void setFromLabel(String fromLabel) {
        this.fromLabel = fromLabel;
    }

    public String getToLabel() {
        return toLabel;
    }

    public void setToLabel(String toLabel) {
        this.toLabel = toLabel;
    }
}
