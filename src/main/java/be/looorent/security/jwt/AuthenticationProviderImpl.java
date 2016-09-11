package be.looorent.security.jwt;


import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Class that verifies the JWT token and when valid, it will set
 * the UserDetails in the authentication object.
 * @author Lorent Lempereur - lorent.lempereur.dev@gmail.com
 */
class AuthenticationProviderImpl implements AuthenticationProvider {

    private final UserDetailsFactory userDetailsFactory;
    private final JwtTokenParser tokenParser;

    @Autowired
    AuthenticationProviderImpl(JwtTokenParser tokenParser, UserDetailsFactory userDetailsFactory) {
        this.tokenParser = tokenParser;
        this.userDetailsFactory = userDetailsFactory;
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        try {
            final UnauthenticatedToken token = (UnauthenticatedToken) authentication;
            final Claims claims = tokenParser.parse(token);
            UserDetails principal = userDetailsFactory.createFrom(claims, token.getRequest());
            return token.authenticate(principal);
        } catch (UserDoesNotExistException e) {
            throw e;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UnauthenticatedToken.class.isAssignableFrom(authentication);
    }
}
