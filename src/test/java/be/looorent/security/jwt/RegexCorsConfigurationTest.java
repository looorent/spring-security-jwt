package be.looorent.security.jwt;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

/**
 * Default tests copied from https://github.com/spring-projects/spring-framework/blob/master/spring-web/src/test/java/org/springframework/web/cors/CorsConfigurationTests.java
 * @author Lorent Lempereur <lorent.lempereur.dev@gmail.com>
 */
public class RegexCorsConfigurationTest {

    private CorsConfiguration cors;
    
    @Before
    public void setup() {
        cors = new RegexCorsConfiguration();
    }

    @Test
    public void checkOriginsWithRegularExpressionHappyPath() {
        String regularExpression = "(https:\\/\\/)([\\-\\w]*)(\\.pouet\\.dev)";
        cors.addAllowedOrigin(regularExpression);
        assertThat(cors.checkOrigin("https://app.pouet.dev"), is(equalTo("https://app.pouet.dev")));
        assertThat(cors.checkOrigin("https://app.pouet.com"), is(nullValue()));
        assertThat(cors.checkOrigin("http://app.pouet.dev"), is(nullValue()));
        assertThat(cors.checkOrigin("https://app.pouet2.dev"), is(nullValue()));
        assertThat(cors.checkOrigin("https://coincoin.pouet.dev"), is(equalTo("https://coincoin.pouet.dev")));
        assertThat(cors.checkOrigin("https://test.pouet.dev"), is(equalTo("https://test.pouet.dev")));
    }

    @Test
    public void setNullValues() {
        cors.setAllowedOrigins(null);
        assertNull(cors.getAllowedOrigins());
        cors.setAllowedHeaders(null);
        assertNull(cors.getAllowedHeaders());
        cors.setAllowedMethods(null);
        assertNull(cors.getAllowedMethods());
        cors.setExposedHeaders(null);
        assertNull(cors.getExposedHeaders());
        cors.setAllowCredentials(null);
        assertNull(cors.getAllowCredentials());
        cors.setMaxAge(null);
        assertNull(cors.getMaxAge());
    }

    @Test
    public void setValues() {
        cors.addAllowedOrigin("*");
        assertEquals(Arrays.asList("*"), cors.getAllowedOrigins());
        cors.addAllowedHeader("*");
        assertEquals(Arrays.asList("*"), cors.getAllowedHeaders());
        cors.addAllowedMethod("*");
        assertEquals(Arrays.asList("*"), cors.getAllowedMethods());
        cors.addExposedHeader("header1");
        cors.addExposedHeader("header2");
        assertEquals(Arrays.asList("header1", "header2"), cors.getExposedHeaders());
        cors.setAllowCredentials(true);
        assertTrue(cors.getAllowCredentials());
        cors.setMaxAge(123L);
        assertEquals(new Long(123), cors.getMaxAge());
    }

    @Test(expected = IllegalArgumentException.class)
    public void asteriskWildCardOnAddExposedHeader() {
        cors.addExposedHeader("*");
    }

    @Test(expected = IllegalArgumentException.class)
    public void asteriskWildCardOnSetExposedHeaders() {
        cors.setExposedHeaders(Arrays.asList("*"));
    }

    @Test
    public void combineWithNull() {
        cors.setAllowedOrigins(Arrays.asList("*"));
        cors.combine(null);
        assertEquals(Arrays.asList("*"), cors.getAllowedOrigins());
    }

    @Test
    public void combineWithNullProperties() {
        cors.addAllowedOrigin("*");
        cors.addAllowedHeader("header1");
        cors.addExposedHeader("header3");
        cors.addAllowedMethod(HttpMethod.GET.name());
        cors.setMaxAge(123L);
        cors.setAllowCredentials(true);
        CorsConfiguration other = new CorsConfiguration();
        cors = cors.combine(other);
        assertEquals(Arrays.asList("*"), cors.getAllowedOrigins());
        assertEquals(Arrays.asList("header1"), cors.getAllowedHeaders());
        assertEquals(Arrays.asList("header3"), cors.getExposedHeaders());
        assertEquals(Arrays.asList(HttpMethod.GET.name()), cors.getAllowedMethods());
        assertEquals(new Long(123), cors.getMaxAge());
        assertTrue(cors.getAllowCredentials());
    }

    @Test
    public void combineWithAsteriskWildCard() {
        cors.addAllowedOrigin("*");
        cors.addAllowedHeader("*");
        cors.addAllowedMethod("*");
        CorsConfiguration other = new CorsConfiguration();
        other.addAllowedOrigin("http://domain.com");
        other.addAllowedHeader("header1");
        other.addExposedHeader("header2");
        other.addAllowedMethod(HttpMethod.PUT.name());
        CorsConfiguration combinedConfig = cors.combine(other);
        assertEquals(Arrays.asList("http://domain.com"), combinedConfig.getAllowedOrigins());
        assertEquals(Arrays.asList("header1"), combinedConfig.getAllowedHeaders());
        assertEquals(Arrays.asList("header2"), combinedConfig.getExposedHeaders());
        assertEquals(Arrays.asList(HttpMethod.PUT.name()), combinedConfig.getAllowedMethods());
        combinedConfig = other.combine(cors);
        assertEquals(Arrays.asList("http://domain.com"), combinedConfig.getAllowedOrigins());
        assertEquals(Arrays.asList("header1"), combinedConfig.getAllowedHeaders());
        assertEquals(Arrays.asList("header2"), combinedConfig.getExposedHeaders());
        assertEquals(Arrays.asList(HttpMethod.PUT.name()), combinedConfig.getAllowedMethods());
    }

    @Test  // SPR-14792
    public void combineWithDuplicatedElements() {
        cors.addAllowedOrigin("http://domain1.com");
        cors.addAllowedOrigin("http://domain2.com");
        cors.addAllowedHeader("header1");
        cors.addAllowedHeader("header2");
        cors.addExposedHeader("header3");
        cors.addExposedHeader("header4");
        cors.addAllowedMethod(HttpMethod.GET.name());
        cors.addAllowedMethod(HttpMethod.PUT.name());
        CorsConfiguration other = new RegexCorsConfiguration();
        other.addAllowedOrigin("http://domain1.com");
        other.addAllowedHeader("header1");
        other.addExposedHeader("header3");
        other.addAllowedMethod(HttpMethod.GET.name());
        CorsConfiguration combinedConfig = cors.combine(other);
        assertEquals(Arrays.asList("http://domain1.com", "http://domain2.com"), combinedConfig.getAllowedOrigins());
        List<String> expected = Arrays.asList("header1", "header2");
        List<String> actual = combinedConfig.getAllowedHeaders();
        assertTrue(expected.containsAll(actual) && actual.containsAll(expected));
        expected = Arrays.asList("header3", "header4");
        actual = combinedConfig.getExposedHeaders();
        assertTrue(expected.containsAll(actual) && actual.containsAll(expected));
        assertEquals(Arrays.asList(HttpMethod.GET.name(), HttpMethod.PUT.name()), combinedConfig.getAllowedMethods());
    }

    @Test
    public void combine() {
        cors.addAllowedOrigin("http://domain1.com");
        cors.addAllowedHeader("header1");
        cors.addExposedHeader("header3");
        cors.addAllowedMethod(HttpMethod.GET.name());
        cors.setMaxAge(123L);
        cors.setAllowCredentials(true);
        CorsConfiguration other = new CorsConfiguration();
        other.addAllowedOrigin("http://domain2.com");
        other.addAllowedHeader("header2");
        other.addExposedHeader("header4");
        other.addAllowedMethod(HttpMethod.PUT.name());
        other.setMaxAge(456L);
        other.setAllowCredentials(false);
        cors = cors.combine(other);
        assertEquals(Arrays.asList("http://domain1.com", "http://domain2.com"), cors.getAllowedOrigins());

        List<String> expected = Arrays.asList("header1", "header2");
        List<String> actual = cors.getAllowedHeaders();
        assertTrue(expected.containsAll(actual) && actual.containsAll(expected));
        expected = Arrays.asList("header3", "header4");
        actual = cors.getExposedHeaders();
        assertTrue(expected.containsAll(actual) && actual.containsAll(expected));
        assertEquals(Arrays.asList(HttpMethod.GET.name(), HttpMethod.PUT.name()), cors.getAllowedMethods());
        assertEquals(new Long(456), cors.getMaxAge());
        assertFalse(cors.getAllowCredentials());
    }

    @Test
    public void checkOriginAllowed() {
        cors.setAllowedOrigins(Arrays.asList("*"));
        assertEquals("*", cors.checkOrigin("http://domain.com"));
        cors.setAllowCredentials(true);
        assertEquals("http://domain.com", cors.checkOrigin("http://domain.com"));
        cors.setAllowedOrigins(Arrays.asList("http://domain.com"));
        assertEquals("http://domain.com", cors.checkOrigin("http://domain.com"));
        cors.setAllowCredentials(false);
        assertEquals("http://domain.com", cors.checkOrigin("http://domain.com"));
    }

    @Test
    public void checkOriginNotAllowed() {
        assertNull(cors.checkOrigin(null));
        assertNull(cors.checkOrigin("http://domain.com"));
        cors.addAllowedOrigin("*");
        assertNull(cors.checkOrigin(null));
        cors.setAllowedOrigins(Arrays.asList("http://domain1.com"));
        assertNull(cors.checkOrigin("http://domain2.com"));
        cors.setAllowedOrigins(new ArrayList<>());
        assertNull(cors.checkOrigin("http://domain.com"));
    }

    @Test
    public void checkMethodAllowed() {
        assertEquals(Arrays.asList(HttpMethod.GET, HttpMethod.HEAD), cors.checkHttpMethod(HttpMethod.GET));
        cors.addAllowedMethod("GET");
        assertEquals(Arrays.asList(HttpMethod.GET), cors.checkHttpMethod(HttpMethod.GET));
        cors.addAllowedMethod("POST");
        assertEquals(Arrays.asList(HttpMethod.GET, HttpMethod.POST), cors.checkHttpMethod(HttpMethod.GET));
        assertEquals(Arrays.asList(HttpMethod.GET, HttpMethod.POST), cors.checkHttpMethod(HttpMethod.POST));
    }

    @Test
    public void checkMethodNotAllowed() {
        assertNull(cors.checkHttpMethod(null));
        assertNull(cors.checkHttpMethod(HttpMethod.DELETE));
        cors.setAllowedMethods(new ArrayList<>());
        assertNull(cors.checkHttpMethod(HttpMethod.POST));
    }

    @Test
    public void checkHeadersAllowed() {
        assertEquals(Collections.emptyList(), cors.checkHeaders(Collections.emptyList()));
        cors.addAllowedHeader("header1");
        cors.addAllowedHeader("header2");
        assertEquals(Arrays.asList("header1"), cors.checkHeaders(Arrays.asList("header1")));
        assertEquals(Arrays.asList("header1", "header2"), cors.checkHeaders(Arrays.asList("header1", "header2")));
        assertEquals(Arrays.asList("header1", "header2"), cors.checkHeaders(Arrays.asList("header1", "header2", "header3")));
    }

    @Test
    public void checkHeadersNotAllowed() {
        assertNull(cors.checkHeaders(null));
        assertNull(cors.checkHeaders(Arrays.asList("header1")));
        cors.setAllowedHeaders(Collections.emptyList());
        assertNull(cors.checkHeaders(Arrays.asList("header1")));
        cors.addAllowedHeader("header2");
        cors.addAllowedHeader("header3");
        assertNull(cors.checkHeaders(Arrays.asList("header1")));
    }


}
