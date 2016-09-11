package be.looorent.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Wraps the logic to verify & parse a JWT and returns its content.
 * @author Lorent Lempereur - lorent.lempereur.dev@gmail.com
 */
class JwtTokenParser {

    private final JwtParser jwtVerifier;

    @Autowired
    JwtTokenParser(AuthenticationProperties authenticationProperties) {
        this.jwtVerifier = Jwts.parser()
                               .setSigningKey(authenticationProperties.getTokenSecretKey())
                               .requireIssuer(authenticationProperties.getTokenIssuer());
    }

    public Claims parse(UnauthenticatedToken token) throws TokenException {
        try {
            return jwtVerifier.parseClaimsJws(token.getJwtAsString()).getBody();
        }
        catch (Exception e) {
            throw new TokenException(e.getMessage());
        }
    }
}
