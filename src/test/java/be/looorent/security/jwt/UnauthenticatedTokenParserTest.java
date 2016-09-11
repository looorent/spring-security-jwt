package be.looorent.security.jwt;

import io.jsonwebtoken.Claims;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class UnauthenticatedTokenParserTest {

    private static final String PUBLIC_ROUTE = "/**";
    private static final String ISSUER = "http://any-arbitraty-issuer.com";
    private static final String SECRET_KEY = "3vIhBhFAXLKfHLWSxHlLm5S9DdFnETX1QZiHOliuiqHAd9hxbbtfgLeNXPXUd6VB";
    private JwtTokenParser parser;
    private UnauthenticatedToken token;
    private Map<String, Object> tokenClaims;

    @Before
    public void setup() throws UnsupportedEncodingException {
        AuthenticationProperties authenticationProperties = new AuthenticationProperties();
        authenticationProperties.setPublicRoute(PUBLIC_ROUTE);
        authenticationProperties.setTokenIssuer(ISSUER);
        authenticationProperties.setTokenSecretKey(SECRET_KEY);
        parser = new JwtTokenParser(authenticationProperties);
        tokenClaims = new HashMap<>();
        tokenClaims.put("FirstName", "Doctor");
        tokenClaims.put("LastName", "Who?");

        token = new UnauthenticatedToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJodHRwOi8vYW55LWFyYml0cmF0eS1pc3N1ZXIuY29tIiwiaWF0IjoxNDczNjA2OTIzLCJleHAiOjE1MDUxNDI5MjMsImF1ZCI6Ind3dy5leGFtcGxlLmNvbSIsInN1YiI6Impyb2NrZXRAZXhhbXBsZS5jb20iLCJGaXJzdE5hbWUiOiJEb2N0b3IiLCJMYXN0TmFtZSI6Ildobz8iLCJFbWFpbCI6Impyb2NrZXRAZXhhbXBsZS5jb20iLCJSb2xlIjpbIk1hbmFnZXIiLCJQcm9qZWN0IEFkbWluaXN0cmF0b3IiXX0.73wY7APrY9CXgPyp-5zCOEnwFyxSYjCpfZC1owN94tmc670lIUBBv0rThbTDeolyjkEUGmub5xg0hhRDTm217g", null);
    }

    @Test
    public void parseClaimsWithValidSecretKeyDoesNotThrowAnyException() {
        Claims claims = parser.parse(token);
        for (Map.Entry<String, Object> values : tokenClaims.entrySet()) {
            assertThat(claims.get(values.getKey()), is(equalTo(values.getValue())));
        }
    }

    @Test(expected = TokenException.class)
    public void parseClaimsWithInvalidSecretKeyThrowsAnException() throws UnsupportedEncodingException {
        AuthenticationProperties wrongProperties = new AuthenticationProperties();
        wrongProperties.setPublicRoute(PUBLIC_ROUTE);
        wrongProperties.setTokenIssuer(ISSUER);
        String wrongToken = "lYdaSlTGinh9wwdZY6pI99Hi4ThAlvxTdDNQQlbsMvQDRyZNQSBhIKXmAN55DmlJykILFmGt9fzF0TdPtRjsywlE7uc5WSSKbrLLJ21GqAvQYJkXGBg4ttLFODp1D1Vq";
        wrongProperties.setTokenSecretKey(wrongToken);
        JwtTokenParser invalidParser = new JwtTokenParser(wrongProperties);
        invalidParser.parse(token);
    }

    @Test(expected = TokenException.class)
    public void parseClaimsWithInvalidIssuerThrowsAnException() throws UnsupportedEncodingException {
        AuthenticationProperties wrongProperties = new AuthenticationProperties();
        wrongProperties.setPublicRoute(PUBLIC_ROUTE);
        wrongProperties.setTokenIssuer("wrong issuer");
        wrongProperties.setTokenSecretKey(SECRET_KEY);
        JwtTokenParser invalidParser = new JwtTokenParser(wrongProperties);
        invalidParser.parse(token);
    }

}
