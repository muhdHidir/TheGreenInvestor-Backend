package G2T6.G2T6.G2T6.payload.response;

import G2T6.G2T6.G2T6.models.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AnswerResponse2 {

    // income value of game stats
    private int changeInIncomeVal = 0;

    // morale value of game stats
    private int changeInMoraleVal = 0;

    // sustainability value of game stats
    private int changeInSustainabilityVal = 0;

    // cash impact of game stats
    private int changeInCashVal = 0;

    // current cash in hand
    private int currentCashVal = 0;

    // current profitability/income/GPT
    private int currentIncomeVal = 0;

    // current sustainability
    private int currentSustainabilityVal = 0;

    // current morale
    private int currentMoraleVal = 0;

    // multiplier
    private double multiplier = 1.0;

    // feedback
    private String feedback = "";

    // Article 
    private Article article = null;

}
