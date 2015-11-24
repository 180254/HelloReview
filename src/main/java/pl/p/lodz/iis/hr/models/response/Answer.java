package pl.p.lodz.iis.hr.models.response;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.MoreObjects;
import pl.p.lodz.iis.hr.models.forms.Input;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Answer implements Serializable {

    private static final long serialVersionUID = -1977277745028715412L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView
    private long id;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @JsonView
    private Response response;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @JsonView
    private Input input;

    @Column(nullable = false, length = 1000)
    @JsonView
    private String answer;

    Answer() {
    }

    public Answer(Input input, String answer) {
        this.input = input;
        this.answer = answer;
    }

    public long getId() {
        return id;
    }

    public Response getResponse() {
        return response;
    }

    /* package */ void setResponse(Response response) {
        this.response = response;
    }

    public Input getInput() {
        return input;
    }

    public String getAnswer() {
        return answer;
    }

    public long getAnswerAsNumber() {
        return Long.valueOf(answer);
    }

    /* package */ void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if ((obj == null) || !(obj instanceof Answer)) return false;
        Answer that = (Answer) obj;
        return getInput().getId() == that.getInput().getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInput().getId());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("input", input.getId())
                .add("answer", answer)
                .add("response", response.getCommission().getId())
                .toString();
    }
}
