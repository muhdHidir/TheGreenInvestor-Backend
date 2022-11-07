package G2T6.G2T6.G2T6.controllers;

import java.util.List;

import javax.validation.Valid;

import G2T6.G2T6.G2T6.exceptions.QuestionNotFoundException;
import G2T6.G2T6.G2T6.models.Option;
import G2T6.G2T6.G2T6.payload.request.AnswerRequest;
import G2T6.G2T6.G2T6.payload.response.AnswerResponse;
import G2T6.G2T6.G2T6.repository.OptionRepository;
import G2T6.G2T6.G2T6.repository.QuestionRepository;
import G2T6.G2T6.G2T6.services.StateService;
import G2T6.G2T6.G2T6.services.similarity.LevenshteinDistanceStrategy;
import G2T6.G2T6.G2T6.services.similarity.StringSimilarityServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import G2T6.G2T6.G2T6.services.similarity.*;
import G2T6.G2T6.G2T6.payload.request.SimilarityRequest;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api")
@RestController
public class AnswerController {
    private StateService stateService;
    private QuestionRepository questionsRepo;
    private OptionRepository optionsRepo;

    @Autowired
    public AnswerController(final StateService stateService, final QuestionRepository questionsRepo,
            final OptionRepository optionsRepo) {
        this.stateService = stateService;
        this.questionsRepo = questionsRepo;
        this.optionsRepo = optionsRepo;
    }

    // give a response to an answer submission
    @PostMapping("/{questionId}/answer")
    public ResponseEntity<?> submitAnswer(@PathVariable(value = "questionId") Long questionId,
            @Valid @RequestBody AnswerRequest answer) {
        try {

            if (answer.isOpenEnded()) {
                StringSimilarityServiceImpl stringSimilarityServiceImpl = new StringSimilarityServiceImpl(
                        new LevenshteinDistanceStrategy());

                // split answer by comma
                String[] answerArray = answer.getAnswer().split(",");

                if (!questionsRepo.existsById(questionId)) {
                    throw new QuestionNotFoundException(questionId);
                }
                List<Option> answerList = optionsRepo.findByQuestionId(questionId);

                // loop through all options and get score with highest
                double highestScore = 0;
                double averageScore = 0; // over three answers so divide by 3

                // loop through answer array
                for (String answerString : answerArray) {
                    // System.out.println(answerString);
                    for (Option option : answerList) {
                        double score = stringSimilarityServiceImpl.score(answerString, option.getOption());
                        // System.out.println("Comparing " + answerString + " and " + option.getOption() + " score: " + score);
                        if (score > highestScore) {
                            highestScore = score;
                        }
                    }
                    averageScore += highestScore / 3.0;
                    highestScore = 0;
                    // System.out.println("average score: " + averageScore);
                }

                return new ResponseEntity<>(averageScore, HttpStatus.OK);

            } else {

                return new ResponseEntity<>("something else", HttpStatus.OK);

            }

        } catch (Exception e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }
    }

}