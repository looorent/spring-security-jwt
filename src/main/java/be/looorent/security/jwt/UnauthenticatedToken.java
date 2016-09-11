package be.looorent.security.jwt;

import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * A token that is not authenticated yet.
 * @author Lorent Lempereur - lorent.lempereur.dev@gmail.com
 */
class UnauthenticatedToken extends Jwt {

    private final HttpServletRequest request;

    /**
     * @param jwt a string representation of the Token. Must not be empty or Base64-encoded.
     */
    UnauthenticatedToken(String jwt, HttpServletRequest request) {
        super(jwt);
        setAuthenticated(false);
        this.request = request;
    }

    @Override
    AuthenticatedToken authenticate(UserDetails principal) {
        return new AuthenticatedToken(getJwtAsString(), principal);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    public HttpServletRequest getRequest() {
        return request;
    }
}
