package G2T6.G2T6.G2T6.payload.response;

public class AnswerResponse {
    private Long questionId;
    private String answer;
    private boolean isOpenEnded;

    public AnswerResponse(Long questionId, String answer, boolean isOpenEnded) {
        this.questionId = questionId;
        this.answer = answer;
        this.isOpenEnded = isOpenEnded;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isOpenEnded() {
        return isOpenEnded;
    }

    public void setOpenEnded(boolean openEnded) {
        isOpenEnded = openEnded;
    }
}
