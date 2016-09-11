package be.looorent.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Cors Properties read from <code>http.headers.*</code>.
 * Used to configure a Cors Filter.
 * @author Lorent Lempereur - lorent.lempereur.dev@gmail.com
 */
@Component
@ConfigurationProperties(prefix = "http.headers")
class HttpHeaderProperties {

    private List<String> allowedMethods = new ArrayList<>();
    private List<String> allowedOrigins = new ArrayList<>();
    private List<String> allowedHeaders = new ArrayList<>();
    private long cacheMaxAge;

    public List<String> getAllowedMethods() {
        return allowedMethods;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public List<String> getAllowedHeaders() {
        return allowedHeaders;
    }

    public long getCacheMaxAge() {
        return cacheMaxAge;
    }

    public void setCacheMaxAge(long cacheMaxAge) {
        this.cacheMaxAge = cacheMaxAge;
    }
}
