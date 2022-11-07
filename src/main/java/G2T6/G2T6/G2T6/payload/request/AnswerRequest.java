package G2T6.G2T6.G2T6.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerRequest {
    private Long questionId;
    private String answer;
    private boolean isOpenEnded;

    public AnswerRequest(Long questionId, String answer, boolean isOpenEnded) {
        this.questionId = questionId;
        this.answer = answer;
        this.isOpenEnded = isOpenEnded;
    }

}
