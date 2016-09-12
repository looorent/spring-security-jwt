[![Build Status](https://travis-ci.org/looorent/spring-security-jwt.svg?branch=master)](https://travis-ci.org/looorent/spring-security-jwt)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/be.looorent/spring-security-jwt/badge.svg)](http://search.maven.org/#artifactdetails%7Cbe.looorent%7Cspring-security-jwt)

# Spring JWT Authentication

This JAR is relevant to you if:
* Your application's users are authenticated using Json Web Token.
* Your application's endpoints must be secured base on these tokens.
* These endpoints are developed using Spring Boot.

## Disclaimer

This JAR is originally developed for my own needs. Do not hesitate to extend it.

## Requirements

* JDK 1.8
* Spring Boot 1.4
* Servlet 3.1

## Choices

* All requests must be authenticated except if they match `authentication.publicRoute`.
* If a required user does not exist, the response has a Status 412 with a header `Authentication-User-Does-Not-Exist` set to `true`.

## Getting started

### 1) Add the JAR to your classpath

Add the JAR to the classpath will enable JWT security automatically if your Spring Boot application enables auto-configurations (_i.e._ 
@EnableAutoConfiguration`).
It is available on _Maven Central_.
To do so, for instance:
* with Gradle:
```groovy
compile "be.looorent:spring-security-jwt:0.2"
```
* or with Maven:
```xml
<dependency>
    <groupId>be.looorent</groupId>
    <artifactId>spring-security-jwt</artifactId>
    <version>0.2</version>
</dependency>
```

### 2) Define the authentication configuration

In your properties, (_e.g._ `application.yml`), 3 properties must be defined:
* `authentication.tokenIssuer`: The JWT issuer, which is check beside the private key. Type: `String` 
* `authentication.tokenSecretKey`: The JWT secret key. This key *must not be Base64-encoded*. Type: `String`
* `authentication.publicRoute`: Ant pattern for routes that do not require a valid token. Do not mind what HTTP method is used. Type: `String`

For instance, in a YAML file using environment properties:
```yaml
authentication:
  tokenIssuer: ${TOKEN_ISSUER}
  tokenSecretKey: ${TOKEN_SECRET_KEY}
  publicRoute: /open/**
```

### 3) Define CORS

This JAR also define a CORS filter on top of each request that is made to your Spring application.
In your properties, (_e.g._ `application.yml`), 4 properties must be defined:
* `http.headers.allowedOrigins`: All allowed origins (_e.g._ a client web application domain).  Type: `List<String>`
* `http.headers.allowedMethods`: All allowed HTTP methods. Type: `List<String>`
* `http.headers.allowedHeaders`: List of headers that a pre-flight request can list as allowed for use during an actual request. `Authorization` must always be present in this list. Type: `List<String>`
* `http.headers.cacheMaxAge`: Configure how long, in seconds, the response from a pre-flight request can be cached by clients. Type: `Long`

For instance, in a YAML file:
```yaml
http:
  headers:
    allowedOrigins:
      - https://your-web-app.io
    allowedMethods:
      - POST
      - PUT
      - GET
      - OPTIONS
      - DELETE
    allowedHeaders:
      - Access-Control-Allow-Headers
      - Origin
      - Accept
      - Authorization
      - X-Requested-With
      - Content-Type
      - Access-Control-Request-Method
      - Access-Control-Request-Headers
    cacheMaxAge: 3600
```

### 4) Provide an implementation of `UserDetailsFactory`

In order to let you handle your String `UserDetails` (structure, permissions, granted authorities, ...), An implementation of `UserDetailsFactory` must be provided. 
This `UserDetails` will be added to the Security Context of each authenticated request.

*This implementation MUST BE registered as a Spring Bean.*


For instance: a Java class can implement this interface to find a User (defined in your own codebase) from the database.
```java
@Service
class UserPrincipalFactoryImpl implements UserDetailsFactory {

    private static final String FACEBOOK_ID_KEY = "facebookId";

    private final UserRepository userRepository;

    @Autowired
    UserPrincipalFactoryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails createFrom(Claims tokenClaims, HttpServletRequest request) throws UserDoesNotExistException {
        User user;
        if (isForUserCreation(request)) {
            user = createUserFromBody(request);
        }
        else {
            user = findUser(tokenClaims);
        }
        return new UserPrincipal(tokenClaims, user);
    }

    private User createUserFromBody(HttpServletRequest request) {
        ...
    }
    
    private boolean isForUserCreation(HttpServletRequest request) {
        ...
    }

    private User findUser(Claims claims) {
        return ofNullable(userRepository.findByXXX(claims.get("XXX")))
                .orElseThrow(() -> new UserDoesNotExistException("User does not exists for this XXX"));
    }
}
```
Where `UserPrincipal` is your own `UserDetails` implementation (which, in this example, contains the user retrieved from database).


### 5) Define a `HandlerMethodArgumentResolver` to always get the current user (Optional)

If you wan to inject your `User` object into your controllers' methods, you can provide an implementation of String's `HandlerMethodArgumentResolver`.
See http://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/method/support/HandlerMethodArgumentResolver.html 

Here you are!


## How to disable this configuration

If your Spring application enables auto-configurations, this security configuration will be enabled by default. To disable it, exclude `JwtSecurityAutoConfiguration` from auto-configurations. 
```
@EnableAutoConfiguration(exclude=[JwtSecurityAutoConfiguration])
@SpringBootApplication
class YourApplicationMainClass {
    ...
}
```

## Future work

* More tests
* More documentation
* Public key support