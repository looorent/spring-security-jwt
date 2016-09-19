package be.looorent.security.jwt;

import org.springframework.security.core.AuthenticationException;

/**
 * Custom implementation of the Exceptions throwed when an Authentication fails.
 * @author Lorent Lempereur - lorent.lempereur.dev@gmail.com
 */
class TokenException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    TokenException(String message) {
        super(message);
    }

    TokenException(JwtExceptionType type) {
        super(type.getCode());
    }

    TokenException(String message, Throwable t) {
        super(message, t);
    }

    TokenException(Exception e) {
        super(e.getMessage(), e);
    }

}