package be.looorent.security.jwt;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.*;

/**
 * Handles an AuthenticationException to check how to respond to the client.
 * @author Lorent Lempereur - lorent.lempereur.dev@gmail.com
 */
class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    private static final String OPTIONS_METHOD = "OPTIONS";
    private static final String USER_DOES_NOT_EXISTS_HEADER = "Authentication-User-Does-Not-Exist";
    private static final String APPLICATION_JSON = "application/json";
    private static final String UTF_8 = "UTF-8";
    private static final String USER_DOES_NOT_EXIST = "user_does_not_exist";
    private static final String TRUE = "true";

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException, ServletException {
        if (isPreflight(request)) {
            response.setStatus(SC_NO_CONTENT);
        }
        else if (authenticationException instanceof UserDoesNotExistException) {
            userDoesNotExistYet(response);
        }
        else if (authenticationException instanceof TokenException) {
            tokenHasBeenRefused(response, (TokenException) authenticationException);
        } else {
            requestIsRefused(response, authenticationException);
        }
    }

    private void requestIsRefused(HttpServletResponse response,
                                  AuthenticationException authException) throws IOException {
        formatResponse(response, SC_FORBIDDEN, authException.getMessage());
    }

    private void tokenHasBeenRefused(HttpServletResponse response,
                                     TokenException authException) throws IOException {
        formatResponse(response, SC_UNAUTHORIZED, authException.getMessage());
    }

    private void userDoesNotExistYet(HttpServletResponse response) throws IOException {
        formatResponse(response, SC_PRECONDITION_FAILED, USER_DOES_NOT_EXIST);
        response.setHeader(USER_DOES_NOT_EXISTS_HEADER, TRUE);
    }

    private void formatResponse(HttpServletResponse response, int status, String reason) throws IOException {
        response.setContentType(APPLICATION_JSON);
        response.setCharacterEncoding(UTF_8);
        response.setStatus(status);
        response.getWriter().write("{\"reason\": \""+reason+"\"}");
    }

    private boolean isPreflight(HttpServletRequest request) {
        return OPTIONS_METHOD.equals(request.getMethod());
    }
}
