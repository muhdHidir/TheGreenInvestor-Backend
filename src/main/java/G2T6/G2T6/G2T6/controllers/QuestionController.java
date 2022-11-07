package G2T6.G2T6.G2T6.controllers;

import java.util.*;

import javax.validation.Valid;

import G2T6.G2T6.G2T6.exceptions.QuestionExistsException;
import G2T6.G2T6.G2T6.exceptions.QuestionNotFoundException;
import G2T6.G2T6.G2T6.models.Question;
import G2T6.G2T6.G2T6.services.QuestionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api")
@RestController
public class QuestionController {
    private QuestionService questionService;
    
    @Autowired
    public QuestionController(final QuestionService questionService){
        this.questionService = questionService;
    }

    // return all questions
    @GetMapping("/questions")
    public List<Question> getQuestion() {
        return questionService.listQuestions();
    }

    // return question by id
    @GetMapping("/questions/{id}")
    public Question getQuestionById(@PathVariable final Long id) throws QuestionNotFoundException {
        Question question = questionService.getQuestion(id);
        if(question == null) throw new QuestionNotFoundException(id);
        return question;
    }

    // add question 
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/questions")
    public Question addQuestion(@RequestBody final Question question) throws QuestionExistsException {
        // returns null if question already exists
        Question savedQuestion = questionService.addQuestion(question); 
        if (savedQuestion ==  null) throw new QuestionExistsException(question.getQuestion());
        return savedQuestion;
    }

    // update question
    @PutMapping("/questions/{id}")
    public Question updateQuestion(@PathVariable final Long id, @Valid @RequestBody final Question newQuestionInfo)  throws QuestionNotFoundException {
        Question question = questionService.updateQuestion(id, newQuestionInfo);
        if(question == null) throw new QuestionNotFoundException(id);
        return question;
    }

    // delete question
    @DeleteMapping("/questions/{id}")
    public void deleteQuestion(@PathVariable final Long id) throws QuestionNotFoundException {
        try{
            questionService.deleteQuestion(id);
        } catch(EmptyResultDataAccessException e) {
            throw new QuestionNotFoundException(id);
        }
    }
}