package be.looorent.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Cors Filter configured using properties from HttpHeaderProperties.
 * @author Lorent Lempereur - lorent.lempereur.dev@gmail.com
 */
class AuthenticationCorsFilter extends CorsFilter {

    @Autowired
    public AuthenticationCorsFilter(HttpHeaderProperties properties) {
        super(createConfiguration(properties));
    }

    private static UrlBasedCorsConfigurationSource createConfiguration(HttpHeaderProperties properties) {
        CorsConfiguration config = new RegexCorsConfiguration();
        config.setAllowCredentials(true);
        properties.getAllowedOrigins().forEach(config::addAllowedOrigin);
        properties.getAllowedMethods().forEach(config::addAllowedMethod);
        properties.getAllowedHeaders().forEach(config::addAllowedHeader);
        config.setMaxAge(properties.getCacheMaxAge());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}