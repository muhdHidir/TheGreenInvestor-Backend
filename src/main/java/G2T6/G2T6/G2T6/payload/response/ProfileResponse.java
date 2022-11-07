package G2T6.G2T6.G2T6.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProfileResponse {

    private double highScore;
    private int gamesPlayed;
    private int profileIndex;

}
