package be.looorent.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import static org.springframework.boot.autoconfigure.security.SecurityProperties.ACCESS_OVERRIDE_ORDER;
import static org.springframework.security.config.http.SessionCreationPolicy.NEVER;

/**
 * Configuration to register as a bean to enable JWT authentication.
 * @author Lorent Lempereur - lorent.lempereur.dev@gmail.com
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(ACCESS_OVERRIDE_ORDER)
@EnableConfigurationProperties({HttpHeaderProperties.class, AuthenticationProperties.class})
public class JwtSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsFactory userDetailsFactory;

    @Autowired
    private HttpHeaderProperties httpHeaderProperties;

    @Autowired
    private AuthenticationProperties authenticationProperties;

    @Override
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Bean(name = "jwtAuthenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationCorsFilter corsFilter() {
        return new AuthenticationCorsFilter(httpHeaderProperties);
    }

    @Bean
    public AuthenticationProviderImpl jwtAuthenticationProvider() {
        return new AuthenticationProviderImpl(tokenParser(), userDetailsFactory);
    }

    @Bean
    @DependsOn
    public JwtTokenParser tokenParser() {
        return new JwtTokenParser(authenticationProperties);
    }

    @Bean
    public AuthenticationEntryPointImpl jwtEntryPoint() {
        return new AuthenticationEntryPointImpl();
    }

    @Bean
    public AuthenticationFilter jwtFilter(final AuthenticationEntryPointImpl entryPoint) throws Exception {
        return new AuthenticationFilter(authenticationManagerBean(), entryPoint);
    }

    /**
     * We do this to ensure our Filter is only loaded once into Application Context
     * <p>
     * If using Spring Boot, any GenericFilterBean in the context will be automatically added to the filter chain.
     * Since we want to support Servlet 2.x and 3.x we should not extend OncePerRequestFilter therefore instead
     * we explicitly define FilterRegistrationBean and disable.
     */
    @Bean
    public FilterRegistrationBean jwtAuthenticationFilterRegistration(final AuthenticationFilter filter) {
        final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(filter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(jwtAuthenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.addFilterAfter(jwtFilter(jwtEntryPoint()), SecurityContextPersistenceFilter.class)
                .addFilterBefore(corsFilter(), AuthenticationFilter.class);
        http.authorizeRequests()
                .antMatchers(authenticationProperties.getPublicRoute()).permitAll()
                .anyRequest().authenticated();
        http.sessionManagement().sessionCreationPolicy(NEVER);
    }
}
