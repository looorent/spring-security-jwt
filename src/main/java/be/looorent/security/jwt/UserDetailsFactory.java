package be.looorent.security.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * The interface you have to provide an implementation.
 * Finds/creates/whatever a UserDetails based on your own needs.
 * This implementation also defines how GrantedAuthorities must be assigned to your UserDetails instance.
 * e.g. retrieves a User from database using the token property.
 * @author Lorent Lempereur - lorent.lempereur.dev@gmail.com
 */
public interface UserDetailsFactory {

    UserDetails createFrom(Claims tokenClaims, HttpServletRequest request) throws UserDoesNotExistException;

}
