package G2T6.G2T6.G2T6.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthHelper {

    public static UserDetails getUserDetails() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // System.out.println(principal);

        if (!(principal instanceof UserDetails)) {
            // System.out.println("did i run here?");
            return null;
        }

        return (UserDetails) principal;

    }


}