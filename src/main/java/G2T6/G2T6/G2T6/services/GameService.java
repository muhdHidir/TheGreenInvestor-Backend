package G2T6.G2T6.G2T6.services;

import java.util.List;

import G2T6.G2T6.G2T6.models.CurrentState;
import G2T6.G2T6.G2T6.models.GameStats;
import G2T6.G2T6.G2T6.models.Question;
import G2T6.G2T6.G2T6.models.orders.OptionOrder;
import G2T6.G2T6.G2T6.payload.response.GameResponse;

public interface GameService {
    void initGame(CurrentState state);
    void nextQuestion(CurrentState currentState);
    void endGame(CurrentState currentState);
    GameResponse getGameInfo(CurrentState state);
    GameResponse getEndGameInfo(CurrentState currentState);
    GameStats getAnsweredStats(CurrentState currentState, int answerIdx);
    GameStats getAnsweredStats(CurrentState currentState, String[] answers);
    CurrentState prepareNextGame(CurrentState currentState);
    List<String> getOptionsList(Question question, OptionOrder optionOrderQ);
    Question getLatestQuestion(CurrentState currentState);
}
