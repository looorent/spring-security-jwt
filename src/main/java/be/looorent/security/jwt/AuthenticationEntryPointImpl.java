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
            tokenHasBeenRefused(response, authenticationException);
        } else {
            requestIsRefused(response, authenticationException);
        }
    }

    private void requestIsRefused(HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.sendError(SC_FORBIDDEN, authException.getMessage());
        response.getWriter().println(formatErrorMessage(authException, SC_FORBIDDEN));
    }

    private void tokenHasBeenRefused(HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.sendError(SC_UNAUTHORIZED, authException.getMessage());
        response.getWriter().println(formatErrorMessage(authException, SC_UNAUTHORIZED));
    }

    private void userDoesNotExistYet(HttpServletResponse response) throws IOException {
        response.sendError(SC_PRECONDITION_FAILED, "This user does not exist.");
        response.setHeader(USER_DOES_NOT_EXISTS_HEADER, "true");
        response.getWriter().println("User does not exists");
    }

    private String formatErrorMessage(Exception authenticationException, int statusCode) {
        return "HTTP Status " + statusCode + " - " + authenticationException.getMessage();
    }

    private boolean isPreflight(HttpServletRequest request) {
        return OPTIONS_METHOD.equals(request.getMethod());
    }
}
