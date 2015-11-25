package pl.p.lodz.iis.hr.models.forms;

import javax.persistence.Entity;

@Entity
//@PrimaryKeyJoinColumn(name = "id")
public class InputText extends Input {

    private static final long serialVersionUID = -8786608199930376985L;

    InputText() {
    }

    public InputText(Question question, String label) {
        super(question, label);
    }
}
