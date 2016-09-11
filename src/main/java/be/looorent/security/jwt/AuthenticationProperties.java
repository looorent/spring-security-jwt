package be.looorent.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * Properties used to do the actual authentication of the JWT.
 * Also provide the route where requests must not be authenticated.
 * @author Lorent Lempereur - lorent.lempereur.dev@gmail.com
 */
@Component
@ConfigurationProperties(prefix = "authentication")
class AuthenticationProperties {

    private String tokenIssuer;
    private byte[] tokenSecretKey;
    private String publicRoute;

    AuthenticationProperties() {}

    public String getTokenIssuer() {
        return tokenIssuer;
    }

    public byte[] getTokenSecretKey() {
        return tokenSecretKey;
    }

    public String getPublicRoute() {
        return publicRoute;
    }

    public void setTokenIssuer(String tokenIssuer) {
        this.tokenIssuer = tokenIssuer;
    }

    public void setTokenSecretKey(String tokenSecretKey) throws UnsupportedEncodingException {
        this.tokenSecretKey = tokenSecretKey.getBytes();
    }

    public void setPublicRoute(String publicRoute) {
        this.publicRoute = publicRoute;
    }
}
