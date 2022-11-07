package G2T6.G2T6.G2T6.services;

import G2T6.G2T6.G2T6.models.Question;

import java.util.List;

public interface QuestionService {
    List<Question> listQuestions();
    Question getQuestion(final Long id);
    Question addQuestion(final Question question);
    Question updateQuestion(final Long id, final Question question);
    void deleteQuestion(final Long id);
    // List<Question> randomizedQuestions();
    // List<Question> initQuestions();
}
