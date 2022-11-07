package G2T6.G2T6.G2T6.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public UserNotFoundException(Long id) {
        super("Could not find user id: " + id);
    }

    public UserNotFoundException(String username) {
        super("Could not find user username: " + username);
    }

}

