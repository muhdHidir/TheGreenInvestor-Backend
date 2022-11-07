package G2T6.G2T6.G2T6.controllers;

import G2T6.G2T6.G2T6.exceptions.StateNotFoundException;
import G2T6.G2T6.G2T6.exceptions.UserNotFoundException;
import G2T6.G2T6.G2T6.misc.State;
import G2T6.G2T6.G2T6.models.*;
import G2T6.G2T6.G2T6.repository.ArticleRepository;
import G2T6.G2T6.G2T6.repository.QuestionRepository;
import G2T6.G2T6.G2T6.repository.StateRepository;
import G2T6.G2T6.G2T6.models.GameStats;
import G2T6.G2T6.G2T6.models.security.User;
import G2T6.G2T6.G2T6.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import G2T6.G2T6.G2T6.payload.request.AnswerRequest2;
import G2T6.G2T6.G2T6.payload.response.AnswerResponse2;
import G2T6.G2T6.G2T6.payload.response.GameResponse;
import G2T6.G2T6.G2T6.payload.response.MessageResponse;
import G2T6.G2T6.G2T6.security.AuthHelper;
import G2T6.G2T6.G2T6.services.GameService;
import io.jsonwebtoken.lang.Arrays;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api")
@RestController
public class GameController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private ArticleRepository articleRepo;

    @Autowired
    private QuestionRepository questionRepo;

    private User currUser;
    private CurrentState currentState;

    /**
     * This method is a helper method to setup the current user and current state
     */
    private void setup() {

        currUser = userRepo.findByUsername(AuthHelper.getUserDetails().getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        currentState = stateRepository.findTopByUserOrderByIdDesc(currUser)
                .orElseThrow(() -> new StateNotFoundException(currUser.getUsername()));
    }

    /**
     * This method is used to fetch the latest game information for a user.
     * 
     * @return GameResponse object that contains the list of questions and options
     *         for the user to answer.
     */
    @GetMapping("/gameInfo")
    public ResponseEntity<?> getGameInfo() {

        setup();

        if (currentState.getCurrentState() == State.start) {

            return ResponseEntity.ok(new GameResponse(State.start, null, 0, null, 0, 0));

        }

        if (currentState.getCurrentState() == State.completed) {
            GameResponse gameResponse = gameService.getEndGameInfo(currentState);
            return ResponseEntity.ok(gameResponse);
        }

        GameResponse gameResponse = gameService.getGameInfo(currentState);
        return ResponseEntity.ok(gameResponse);

    }

    /**
     * This method starts the game
     * if state is start, change existing state to answering
     * if state is answering, create new state instance and change state to
     * answering
     * if state is completed, create new state instance and change state to
     * answering
     * 
     * @return ResponseEntity<?>
     */
    @PostMapping("/startGame")
    public ResponseEntity<?> startGame() {

        setup();

        State state = currentState.getCurrentState();

        if (state == State.start) {

            gameService.initGame(currentState);

            return ResponseEntity.ok(new MessageResponse("Game started"));

        }

        CurrentState newState = gameService.prepareNextGame(currentState);
        gameService.initGame(newState);

        if (state == State.completed) {

            return ResponseEntity.ok(new MessageResponse("Game started | previous game has ended successfully"));

        }

        return ResponseEntity.ok(new MessageResponse("Game started | previous game terminated prematurely"));

    }

    /**
     * This method is submitAnswer for question (both MCQ and open-ended)
     * 
     * @param answerRequest
     * @return ResponseEntity<?>
     */
    @PostMapping("/submitAnswer")
    public ResponseEntity<?> submitAnswer(@Valid @RequestBody AnswerRequest2 answerRequest) {

        setup();

        if (currentState.getCurrentState() != State.answering || currentState.getYearValue() >= 10) {
            gameService.endGame(currentState, true);
            return ResponseEntity.badRequest().body(new MessageResponse("Game has not started or Game has ended"));
        }

        Question question = gameService.getLatestQuestion(currentState);
        boolean isOpenEnded = question.isOpenEnded();

        try {

            GameStats gameStats = null;
            int answer = 0;

            // try to parse given answer
            if (!isOpenEnded) {

                answer = Integer.parseInt(answerRequest.getAnswer());
                gameStats = gameService.getAnsweredStats(currentState, answer);

            } else {

                String[] answers = answerRequest.getAnswer().split(",");
                gameStats = gameService.getAnsweredStats(currentState, answers);

            }

            currentState.setUserResponse(answerRequest.getAnswer());
            stateRepository.saveAndFlush(currentState);

            // update current vals
            int currentCashInHand = gameStats.getCurrentCashInHand();
            int currentIncomeVal = gameStats.getCurrentIncomeVal();
            int currentEmissionVal = gameStats.getCurrentSustainabilityVal();
            int currentMoraleVal = gameStats.getCurrentMoraleVal();

            // to find the correct index that matches the options order
            int idx = 0;
            for (int i = 0; i < currentState.getQuestionOrder().getIndexArray().size(); i++) {
                if ((currentState.getQuestionOrder().getIndexArray().get(i) + 1) == question.getId().intValue()) {
                    idx = i;
                }
            }

            int optionIdx = currentState.getQuestionOrder().getOptionOrders().get(idx)
                    .getIndexArray().get(answer);

            AnswerResponse2 answerResponse = new AnswerResponse2(gameStats.getCurrentIncomeVal(),
                    gameStats.getChangeInMoraleVal(),
                    gameStats.getChangeInSustainabilityVal(), gameStats.getChangeInCashVal(), currentCashInHand,
                    currentIncomeVal,
                    currentEmissionVal, currentMoraleVal, gameStats.getMultiplier(),
                    isOpenEnded ? questionRepo.findById(question.getId()).get().getOptions().get(0).getFeedback()
                            : question.getOptions().get(optionIdx).getFeedback(),
                    question.getArticle());

            if (currentState.checkIfGameShouldEnd()) {
                if (currentState.getYearValue() == 9) {
                    gameService.endGame(currentState, true);
                } else {
                    gameService.endGame(currentState, false);
                }
                return ResponseEntity.ok(answerResponse);
            }

            // Next Question
            gameService.nextQuestion(currentState);
            return ResponseEntity.ok(answerResponse);

        } catch (Exception e) {

            e.printStackTrace();
            gameService.endGame(currentState, false);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: An error has occurred | game ended or wrong input"));

        }
    }

}
