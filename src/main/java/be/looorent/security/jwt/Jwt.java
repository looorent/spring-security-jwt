package be.looorent.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

/**
 * Defines a Json Web Token wrapper, authenticated or not.
 * @author Lorent Lempereur
 */
abstract class Jwt extends AbstractAuthenticationToken {

    private final char[] jwt;

    /**
     * @param jwt a string representation of the Token. Must not be empty or Base64-encoded.
     */
    Jwt(String jwt) {
        super(null);
        if (StringUtils.isEmpty(jwt)) {
            throw new IllegalArgumentException("A String representation of JWT is required. Currently empty.");
        }
        this.jwt = jwt.toCharArray();
    }

    public String getJwtAsString() {
        return String.valueOf(jwt);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    abstract AuthenticatedToken authenticate(UserDetails principal);
}
