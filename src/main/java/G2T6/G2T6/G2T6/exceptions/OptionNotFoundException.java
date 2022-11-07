package G2T6.G2T6.G2T6.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OptionNotFoundException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public OptionNotFoundException(Long id) {
        super("Could not find option " + id);
    }
}
