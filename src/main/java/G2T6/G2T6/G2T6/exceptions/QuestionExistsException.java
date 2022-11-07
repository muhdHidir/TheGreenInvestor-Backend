package G2T6.G2T6.G2T6.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class QuestionExistsException extends RuntimeException{
    
    private static final long serialVersionUID = 1L;

    public QuestionExistsException(String question) {
        super("This Question exists: " + question);
    }
}
