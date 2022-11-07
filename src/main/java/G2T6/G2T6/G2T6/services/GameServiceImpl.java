package G2T6.G2T6.G2T6.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import G2T6.G2T6.G2T6.exceptions.QuestionNotFoundException;
import G2T6.G2T6.G2T6.models.CurrentState;
import G2T6.G2T6.G2T6.models.GameStats;
import G2T6.G2T6.G2T6.models.Option;
import G2T6.G2T6.G2T6.models.Question;
import G2T6.G2T6.G2T6.models.orders.OptionOrder;
import G2T6.G2T6.G2T6.models.orders.QuestionOrder;
import G2T6.G2T6.G2T6.payload.response.GameResponse;
import G2T6.G2T6.G2T6.payload.response.QuestionAndOptions;
import G2T6.G2T6.G2T6.repository.GameStatsRepository;
import G2T6.G2T6.G2T6.repository.OptionRepository;
import G2T6.G2T6.G2T6.repository.QuestionOrderRepository;
import G2T6.G2T6.G2T6.repository.QuestionRepository;
import G2T6.G2T6.G2T6.repository.StateRepository;
import G2T6.G2T6.G2T6.repository.UserRepository;
import G2T6.G2T6.G2T6.services.similarity.LevenshteinDistanceStrategy;
import G2T6.G2T6.G2T6.services.similarity.StringSimilarityServiceImpl;
import G2T6.G2T6.G2T6.misc.*;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private StateRepository stateRepo;

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private GameStatsRepository gameStatsRepo;

    @Autowired
    private QuestionOrderRepository questionOrderRepo;

    @Autowired
    private OptionRepository optionRepo;

    @Autowired
    private StateService stateService;

    @Autowired
    private UserRepository userRepo;

    /**
     * This method is used to initialize the game. It will create a new game state
     * for user that has current state as "start"
     * 
     * @param currentState the current state of the user
     */
    @Override
    public void initGame(CurrentState currentState) {

        QuestionOrder newOrder = new QuestionOrder();
        GameStats newGameStats = new GameStats();
        currentState.setQuestionOrder(newOrder);
        // change to answering because user has started the game
        currentState.setCurrentState(State.answering);

        // set up relationship for GameStats object
        newGameStats.setCurrentState(currentState);
        newGameStats.setUser(currentState.getUser());
        newGameStats.setMultiplier(1.0); // Base Multiplier

        currentState.setGameStats(newGameStats);
        ArrayList<CurrentState> states = new ArrayList<CurrentState>();
        states.add(currentState);
        newOrder.setCurrentStates(states);

        questionOrderRepo.saveAndFlush(newOrder);

        stateRepo.saveAndFlush(currentState);

    }

    /**
     * This method is used to retrieve the most recent game information
     * 
     * @param currentState the current state of the user
     * @return the game response object
     */
    @Override
    public GameResponse getGameInfo(CurrentState currentState) {

        QuestionOrder questionOrder = currentState.getQuestionOrder();

        List<QuestionAndOptions> questionAndOptions = getQuestionAndOptionsList(questionOrder);

        // get past game stats
        List<CurrentState> pastState = stateRepo.findByGameIdAndUserId(currentState.getGameId(),
                currentState.getUser().getId());
        List<GameStats> pastGameStats = new ArrayList<GameStats>();
        // iterate through pastState if it exists
        if (pastState != null) {
            for (CurrentState state : pastState) {
                pastGameStats.add(gameStatsRepo.findByCurrentState(state));
            }
        }

        double totalScore = calculateTotalScore(currentState);

        GameResponse gameResponse = new GameResponse(currentState.getCurrentState(), questionAndOptions,
                currentState.getYearValue(), pastGameStats,
                currentState.getGameStats() != null ? currentState.getGameStats().getMultiplier() : 1.0, totalScore);

        return gameResponse;
    }

    /**
     * This method returns the game info when the game ends
     * 
     * @param currentState the current state of the user
     * @return the game response object
     */
    @Override
    public GameResponse getEndGameInfo(CurrentState currentState) {

        // get past game stats
        List<CurrentState> pastState = stateRepo.findByGameIdAndUserId(currentState.getGameId(),
                currentState.getUser().getId());
        List<GameStats> pastGameStats = new ArrayList<GameStats>();
        // iterate through pastState if it exists
        if (pastState != null) {
            for (CurrentState state : pastState) {
                pastGameStats.add(gameStatsRepo.findByCurrentState(state));
            }
        }

        // calculate totalscore
        double totalScore = calculateTotalScore(currentState);

        GameResponse gameResponse = new GameResponse(State.completed, null, currentState.getYearValue(),
                pastGameStats, currentState.getGameStats() == null ? 1.0 : currentState.getGameStats().getMultiplier(),
                totalScore);

        return gameResponse;
    }

    /**
     * This method is used to end the game. It will update the highscore and
     * increment total game of user by 1
     * 
     * @param currentState the current state of the user
     */
    @Override
    public void endGame(CurrentState currentState) {
        if (currentState.getGameStats() != null) {
            // save highscore
            if (currentState.getGameStats().getTotalScore() > currentState.getUser().getHighScore()) {
                currentState.getUser().setHighScore(currentState.getGameStats().getTotalScore());
                userRepo.saveAndFlush(currentState.getUser());
            }
            // add one to total game count of user
            currentState.getUser().setGamesPlayed((currentState.getUser().getGamesPlayed() + 1));
        }
        // change the state to completed
        if (currentState.getCurrentState() != State.completed) {
            currentState.setCurrentState(State.completed);
            stateRepo.saveAndFlush(currentState);
        }
    }

    /**
     * This method is used to get the most recent question the user is on
     * 
     * @param currentState the current state of the user
     * @return the question object
     */
    @Override
    public Question getLatestQuestion(CurrentState currentState) {
        QuestionOrder questionOrder = currentState.getQuestionOrder();
        Long questionNumber = (long) questionOrder.getIndexArray().get(currentState.getYearValue());
        Question question = questionRepo.findById(questionNumber + 1)
                .orElseThrow(() -> new QuestionNotFoundException(questionNumber));
        return question;
    }

    /**
     * Handles submit Answer for MCQ questions
     * 
     * @param currentState
     * @param {number}     answerIdx
     * @return GameStats
     */
    @Override
    public GameStats getAnsweredStats(CurrentState currentState, int answerIdx) {

        QuestionOrder questionOrder = currentState.getQuestionOrder();
        int year = currentState.getYearValue(); // also can be used for question index it is currently on

        Long questionNumber = (long) questionOrder.getIndexArray().get(year);

        Question question = questionRepo.findById(questionNumber + 1)
                .orElseThrow(() -> new QuestionNotFoundException(questionNumber));

        Option option = question.getOptions()
                .get(questionOrder.getOptionOrders().get(year).getIndexArray().get(answerIdx));

        int sustainabilityImpact = option.getSustainabilityImpact();
        int moraleImpact = option.getMoraleImpact();
        int incomeImpact = option.getIncomeImpact();
        int costImpact = option.getCostImpact();

        // currentGameStats is not supposed to be null, replace over existing
        // placeholder currentGameStats
        GameStats currentGameStats = currentState.getGameStats();
        int newCashInHand = currentGameStats.getCurrentCashInHand() + currentGameStats.getCurrentIncomeVal()
                - costImpact;
        int newMorale = currentGameStats.getCurrentMoraleVal() + moraleImpact;
        int newSustainability = currentGameStats.getCurrentSustainabilityVal() + sustainabilityImpact;
        int newIncomeImpact = currentGameStats.getCurrentIncomeVal() + incomeImpact;
        currentGameStats.setChangeInSustainabilityVal(sustainabilityImpact);
        currentGameStats.setChangeInMoraleVal(moraleImpact);
        currentGameStats.setChangeInIncomeVal(incomeImpact);
        currentGameStats.setChangeInCashVal(costImpact);
        currentGameStats.setCurrentCashInHand(newCashInHand);
        currentGameStats.setCurrentSustainabilityVal(newSustainability);
        currentGameStats.setCurrentIncomeVal(newIncomeImpact);
        currentGameStats.setCurrentMoraleVal(newMorale);
        currentGameStats.setMultiplier(1.0 * currentGameStats.getMultiplier()); // no multiplier for normal question
        currentGameStats.setTotalScore(calculateTotalScore(currentState));

        gameStatsRepo.saveAndFlush(currentGameStats);

        return currentGameStats;

    }

    /**
     * Handles submit Answer for open ended questions
     * 
     * @param currentState
     * @param answer
     * @return {String array} GameStats
     */
    @Override
    public GameStats getAnsweredStats(CurrentState currentState, String[] answers) {

        QuestionOrder questionOrder = currentState.getQuestionOrder();
        int year = currentState.getYearValue(); // also can be used for question index it is currently on

        Long questionNumber = (long) questionOrder.getIndexArray().get(year);

        List<Option> answerList = optionRepo.findByQuestionId(questionNumber + 1);

        // loop through all options and get score with highest
        double highestScore = 0;
        double averageScore = 0; // over three answers so divide by 3

        StringSimilarityServiceImpl stringSimilarityServiceImpl = new StringSimilarityServiceImpl(
                new LevenshteinDistanceStrategy());

        // loop through answer array
        for (String answerString : answers) {

            // System.out.println(answerString);
            for (Option option : answerList) {
                double score = stringSimilarityServiceImpl.score(answerString, option.getOption());
                // System.out.println("Comparing " + answerString + " and " + option.getOption()
                // + " score: " + score);
                if (score > highestScore) {
                    highestScore = score;
                }
            }
            averageScore += highestScore / 3.0;
            highestScore = 0;
            // System.out.println("average score: " + averageScore);
        }

        GameStats currentGameStats = currentState.getGameStats();

        // currentGameStats is not supposed to be null, replace over existing
        // placeholder currentGameStats
        currentGameStats.setMultiplier(currentGameStats.getMultiplier() * (averageScore + 1.0));
        currentGameStats.setTotalScore(calculateTotalScore(currentState));

        gameStatsRepo.saveAndFlush(currentGameStats);

        return currentGameStats;

    }

    /**
     * This method moves the user to the next question
     * 
     * @param currentState the current state of the user
     */
    @Override
    public void nextQuestion(CurrentState currentState) {

        GameStats oldGameStats = currentState.getGameStats();
        currentState.setCurrentState(State.nullState);

        int year = currentState.getYearValue(); // also can be used for question index it is currently on
        int nextYear = year + 1;

        CurrentState newState = new CurrentState(currentState.getGameId(), currentState.getUser(), nextYear,
                State.answering, currentState.getQuestionOrder());

        GameStats newGameStats = new GameStats(currentState.getUser(), newState, oldGameStats.getMultiplier(),
                oldGameStats.getCurrentSustainabilityVal(), oldGameStats.getCurrentMoraleVal(),
                oldGameStats.getCurrentIncomeVal(),
                oldGameStats.getCurrentCashInHand());

        newState.setGameStats(newGameStats);
        stateRepo.saveAndFlush(currentState);
        stateRepo.saveAndFlush(newState);

    }

    /**
     * This method prepares next game, sets current state to completed and creates a
     * new game state
     * 
     * @param currentState the current state of the user
     * @return the new game state
     */
    @Override
    public CurrentState prepareNextGame(CurrentState currentState) {

        // change old state to completed and save
        currentState.setCurrentState(State.completed);
        // give the old state a total score
        currentState.getGameStats().setTotalScore(calculateTotalScore(currentState));
        stateRepo.saveAndFlush(currentState);

        CurrentState newState = stateService.getDefaultState();

        Long currGameId = currentState.getGameId();
        Long nextGameId = currGameId + 1;

        // set gameId to next game
        newState.setGameId(nextGameId);
        newState.setUser(currentState.getUser());

        QuestionOrder newOrder = new QuestionOrder();
        newState.setQuestionOrder(newOrder);

        ArrayList<CurrentState> states = new ArrayList<CurrentState>();
        states.add(newState);
        newOrder.setCurrentStates(states);

        // save the new order
        questionOrderRepo.saveAndFlush(newOrder);

        stateRepo.saveAndFlush(newState);

        return newState;
    }

    /**
     * This methods gets the 4 options for the current question based on the option
     * order
     * 
     * @param question
     * @param optionOrder
     * @return {String array} the 4 options for the current question
     */
    @Override
    public List<String> getOptionsList(Question question, OptionOrder optionOrder) {

        ArrayList<String> optionsName = new ArrayList<String>();

        for (int i = 0; i < 4; i++) {
            optionsName.add(question.getOptions().get(optionOrder.getIndexArray().get(i)).getOption());
        }

        return optionsName;

    }

    /**
     * This method calculates the total score for the game
     * 
     * @param currentState
     * @return {double} the total score
     */
    private double calculateTotalScore(CurrentState currentState) {
        GameStats gameStats = currentState.getGameStats();
        double totalScore = gameStats.getCurrentCashInHand() + gameStats.getCurrentIncomeVal()
                + gameStats.getCurrentMoraleVal() + (3 * gameStats.getCurrentSustainabilityVal());
        totalScore *= gameStats.getMultiplier();
        // round totalScore to 2 decimal place
        totalScore = Math.round(totalScore * 100.0) / 100.0;
        return totalScore;
    }

    /**
     * This method gets the List of Question and Options
     * 
     * @param questionOrder
     * @return {List<QuestionAndOptions>} the list of questions and options
     */
    private List<QuestionAndOptions> getQuestionAndOptionsList(QuestionOrder questionOrder) {

        List<QuestionAndOptions> questionAndOptions = new ArrayList<QuestionAndOptions>();

        for (int i = 0; i < questionOrder.getIndexArray().size(); i++) {
            Long questionNumber = (long) questionOrder.getIndexArray().get(i);

            Question question = questionRepo.findById(questionNumber + 1)
                    .orElseThrow(() -> new QuestionNotFoundException(questionNumber));

            String questionName = question.getQuestion();
            List<String> optionsName = getOptionsList(question, questionOrder.getOptionOrders().get(i));
            boolean isOpenEnded = question.isOpenEnded();

            if (question.isOpenEnded())
                optionsName = null;

            QuestionAndOptions questionsAndOptions = new QuestionAndOptions(questionName, question.getImageLink(),
                    optionsName, isOpenEnded);
            questionAndOptions.add(questionsAndOptions);
        }

        return questionAndOptions;
    }

}
