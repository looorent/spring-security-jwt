package be.looorent.security.jwt;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Lorent Lempereur - lorent.lempereur.dev@gmail.com
 */
public class UserDoesNotExistException extends AuthenticationException {
    private static final long serialVersionUID = 1L;

    public UserDoesNotExistException(String message) {
        super(message);
    }

    UserDoesNotExistException(String message, Throwable t) {
        super(message, t);
    }

    UserDoesNotExistException(Exception e) {
        super(e.getMessage(), e);
    }
}
