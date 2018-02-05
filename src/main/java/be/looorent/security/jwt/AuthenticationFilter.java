package be.looorent.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter responsible to intercept the JWT in the HTTP header and attempt an authentication.
 * It delegates the authentication to the authentication manager.
 * @author Lorent Lempereur - lorent.lempereur.dev@gmail.com
 */
class AuthenticationFilter extends GenericFilterBean {

    private final Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);

    private static final String OPTIONS_METHOD = "OPTIONS";
    private static final String BEARER_SCHEME = "Bearer";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String NO_AUTHORIZATION_HEADER = "Unauthorized: No Authorization header was found";
    private static final String WRONG_AUTHORIZATION_HEADER_FORMAT = "Unauthorized: Format is Authorization: Bearer [token]";

    private final AuthenticationEntryPoint entryPoint;
    private final AuthenticationManager authenticationManager;

    AuthenticationFilter(AuthenticationManager authenticationManager,
                         AuthenticationEntryPoint entryPoint) {
        if (authenticationManager == null) {
            throw new IllegalArgumentException("authenticationManager must not be null");
        }
        if (entryPoint == null) {
            throw new IllegalArgumentException("entryPoint must not be null");
        }
        this.entryPoint = entryPoint;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (!httpRequest.getMethod().equals(OPTIONS_METHOD)) {
            try {
                final UnauthenticatedToken token = readTokenFrom(httpRequest);
                final Authentication authenticationResult = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(authenticationResult);
            }
            catch (AuthenticationException failed) {
                SecurityContextHolder.clearContext();
                entryPoint.commence(httpRequest, httpResponse, failed);
                return;
            }
        }
        chain.doFilter(httpRequest, httpResponse);
    }

    private UnauthenticatedToken readTokenFrom(HttpServletRequest httpRequest) {
        try {
            return new UnauthenticatedToken(extractTokenFrom(httpRequest), httpRequest);
        }
        catch (IllegalArgumentException e) {
            LOG.trace("Impossible to get Authorization header: {}", e.getMessage());
            throw new TokenException("jwt_missing_bearer_token", e);
        }
    }

    private String extractTokenFrom(HttpServletRequest httpRequest) {
        final String authorizationHeader = httpRequest.getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader == null) {
            throw new IllegalArgumentException(NO_AUTHORIZATION_HEADER);
        }

        final String[] parts = authorizationHeader.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException(WRONG_AUTHORIZATION_HEADER_FORMAT);
        }

        final String scheme = parts[0];
        final String token = parts[1];
        if (BEARER_SCHEME.equalsIgnoreCase(scheme)) {
            return token;
        }
        else {
            throw new IllegalArgumentException("Wrong Scheme: "+scheme+". Expected: " + BEARER_SCHEME);
        }
    }
}