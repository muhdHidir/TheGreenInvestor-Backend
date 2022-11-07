package G2T6.G2T6.G2T6.payload.response;

import java.util.ArrayList;
import java.util.List;

import G2T6.G2T6.G2T6.misc.State;
import G2T6.G2T6.G2T6.models.GameStats;
import G2T6.G2T6.G2T6.models.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GameResponse {

    // latest state
    private State state;

    // list of questions and options
    private List<QuestionAndOptions> questionsAndOptions;

    /// current year
    private int year;

    // past game stats
    private List<GameStats> stats;

    // latest multiplier
    private double multiplier;

    // total score
    private double totalScore;

}
