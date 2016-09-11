package be.looorent.security.jwt;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Scan package and enable the overall security configuration.
 * @author Lorent Lempereur - lorent.lempereur.dev@gmail.com
 */
@Configuration
@ComponentScan(basePackageClasses = JwtSecurityConfiguration.class)
public class JwtSecurityAutoConfiguration {
}
