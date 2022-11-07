package G2T6.G2T6.G2T6.services;

import java.util.*;

import G2T6.G2T6.G2T6.models.Option;
import G2T6.G2T6.G2T6.models.Question;
import G2T6.G2T6.G2T6.models.orders.OptionOrder;
import G2T6.G2T6.G2T6.repository.QuestionRepository;
import G2T6.G2T6.G2T6.services.order.OptionOrderService;
import G2T6.G2T6.G2T6.services.order.QuestionOrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceImpl implements QuestionService {
    private QuestionRepository questionRepository;

    @Autowired
    public QuestionServiceImpl(final QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    // finds all questions in questionRepository
    @Override
    public List<Question> listQuestions() {
        return questionRepository.findAll();
    }
    
    // find a specific question by its ID 
    @Override
    public Question getQuestion(final Long id) {
        return questionRepository.findById(id).orElse(null);
    }
    
    // add a new questions to questionRepository, else return null
    @Override
    public Question addQuestion(final Question question) {
        // checks if question already exists (same body)
        List<Question> sameQuestion = questionRepository.findByQuestion(question.getQuestion());
        // if question isn't found, save it
        if(sameQuestion.size() == 0) {
            return questionRepository.save(question);
        }
        return null;
    }
    
    // update existing question with new question, else return null
    @Override
    public Question updateQuestion(final Long id, final Question newQuestion) {
        // find question by id
        return questionRepository.findById(id).map(question -> {
            // update question with variables of newQuestion
            question.setQuestion(newQuestion.getQuestion());
            question.setImageLink(newQuestion.getImageLink());
            question.setOpenEnded(newQuestion.isOpenEnded());
            // save updated question
            return questionRepository.save(question);
        }).orElse(null);
    }
    
    // delete question by id if exists
    @Override
    public void deleteQuestion(final Long id) {
        questionRepository.deleteById(id);
    }
}