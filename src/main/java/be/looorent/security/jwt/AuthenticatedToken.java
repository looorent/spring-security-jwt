package be.looorent.security.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * A JWT that has been authenticated by an AuthenticationProvider.
 * @author Lorent Lempereur - lorent.lempereur.dev@gmail.com
 */
class AuthenticatedToken extends Jwt {

    private final UserDetails principal;

    AuthenticatedToken(String jwt, UserDetails principal) {
        super(jwt);
        this.principal = principal;
        setDetails(principal);
        setAuthenticated(true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> getAuthorities() {
        return (Collection<GrantedAuthority>) principal.getAuthorities();
    }


    @Override
    public UserDetails getPrincipal() {
        return principal;
    }

    @Override
    AuthenticatedToken authenticate(UserDetails principal) {
        return this;
    }
}
