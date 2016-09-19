package be.looorent.security.jwt;


import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;

import static be.looorent.security.jwt.JwtExceptionType.*;

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
        catch (UnsupportedJwtException e) {
            throw new TokenException(UNSUPPORTED);
        }
        catch (MalformedJwtException e) {
            throw new TokenException(MALFORMED);
        }
        catch (SignatureException e) {
            throw new TokenException(WRONG_SIGNATURE);
        }
        catch (ExpiredJwtException e) {
            throw new TokenException(EXPIRED);
        }
        catch (Exception e) {
            throw new TokenException(e.getMessage());
        }
    }
}
