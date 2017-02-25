package be.looorent.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.cors.CorsConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Extend the traditional CORS origin check (equalsIgnoreCase) with a regular expression check.
 * @author Lorent Lempereur <lorent.lempereur.dev@gmail.com>
 */
class RegexCorsConfiguration extends CorsConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegexCorsConfiguration.class);
    private final List<Pattern> allowedOriginsRegex;

    RegexCorsConfiguration() {
        allowedOriginsRegex = new ArrayList<>();
    }

    RegexCorsConfiguration(CorsConfiguration other) {
        this();
        setAllowCredentials(other.getAllowCredentials());
        setAllowedOrigins(other.getAllowedOrigins());
        setAllowedHeaders(other.getAllowedHeaders());
        setAllowedMethods(other.getAllowedMethods());
        setExposedHeaders(other.getExposedHeaders());
        setMaxAge(other.getMaxAge());
    }

    @Override
    public void addAllowedOrigin(String origin) {
        super.addAllowedOrigin(origin);
        try {
            allowedOriginsRegex.add(Pattern.compile(origin));
        }
        catch(PatternSyntaxException e) {
            LOGGER.warn("Wrong syntax for the origin {} as a regular expression. If this origin is not supposed to be a regular expression, just ignore this error.", origin);
        }
    }

    @Override
    public String checkOrigin(String requestOrigin) {
        String result = super.checkOrigin(requestOrigin);
        return result != null ? result : checkOriginWithRegularExpression(requestOrigin);
    }

    private String checkOriginWithRegularExpression(String requestOrigin) {
        return allowedOriginsRegex.stream()
                .filter(pattern -> pattern.matcher(requestOrigin).matches())
                .map(pattern -> requestOrigin)
                .findFirst()
                .orElse(null);
    }

    @Override
    public CorsConfiguration combine(CorsConfiguration other) {
        if (other == null) {
            return this;
        }
        CorsConfiguration config = new RegexCorsConfiguration(this);
        config.setAllowedOrigins(combine(this.getAllowedOrigins(), other.getAllowedOrigins()));
        config.setAllowedMethods(combine(this.getAllowedMethods(), other.getAllowedMethods()));
        config.setAllowedHeaders(combine(this.getAllowedHeaders(), other.getAllowedHeaders()));
        config.setExposedHeaders(combine(this.getExposedHeaders(), other.getExposedHeaders()));
        Boolean allowCredentials = other.getAllowCredentials();
        if (allowCredentials != null) {
            config.setAllowCredentials(allowCredentials);
        }
        Long maxAge = other.getMaxAge();
        if (maxAge != null) {
            config.setMaxAge(maxAge);
        }
        return config;
    }

    private List<String> combine(List<String> source, List<String> other) {
        if (other == null || other.contains(ALL)) {
            return source;
        }
        if (source == null || source.contains(ALL)) {
            return other;
        }
        Set<String> combined = new HashSet<>(source);
        combined.addAll(other);
        return new ArrayList<>(combined);
    }
}
