package G2T6.G2T6.G2T6.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class OptionExistsException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public OptionExistsException(String option) {
        super("This Option exists: " + option);
    }
}
