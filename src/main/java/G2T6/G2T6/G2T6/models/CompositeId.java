package G2T6.G2T6.G2T6.models;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Id;

@Embeddable
public class CompositeId implements Serializable {
    private Long qid;
    private Long oid;

    public CompositeId(Question question, Option option) {
        this.qid = question.getId();
        this.oid = option.getId();
    }
}
