package G2T6.G2T6.G2T6.payload.response;
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
public class QuestionAndOptions {

    // latest question
    private String questionName;

    // image path
    private String imagePath;

    // options for the latest question
    private List<String> optionsName;

    // is open ended?
    private boolean openEnded;

    
}
