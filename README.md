# Spring JWT Authentication

*This is a DRAFT.* 

This JAR is originally developed for my own needs. Do not hesitate to extend it.

## Requirements

* JDK 1.8
* Spring Boot 1.4

## Hypotheses

* All requests must be authenticated except the ones prefixed using `authentication.publicRoute`.
* Granted authorities are Role-based.

## Implementations to provide

* An implementation of `UserDetailsFactory` must be provided to find/create/... your own implementation of Spring's `UserDetails` (which will be added to the Security Context of each authenticated request).

## Enable this configuration

* For now, no autoconfiguration class is provided. Therefore, you have to enable ComponentScan on package `be.looorent.security.jwt`

## Responses

* If a required user does not exist, the response has a Status 412 with a header `Authentication-User-Does-Not-Exist` set to `true`.