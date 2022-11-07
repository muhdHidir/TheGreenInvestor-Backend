package G2T6.G2T6.G2T6.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotEnoughGameStatsException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public NotEnoughGameStatsException(int id) {
        super("Could not enough game stats to display " + id + " stats");
    }

}
