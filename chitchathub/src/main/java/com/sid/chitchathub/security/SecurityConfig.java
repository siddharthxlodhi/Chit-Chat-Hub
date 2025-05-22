package com.sid.chitchathub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers("/auth/**",
                                        "/v2/api-docs",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-resources",
                                        "/swagger-resources/**",
                                        "/configuration/ui",
                                        "/configuration/security",
                                        "/swagger-ui/**",
                                        "/webjars/**",
                                        "/swagger-ui.html",
                                        "/ws/**",
                                        "/free/**")
                                .permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(auth ->
                        auth.jwt(token ->
                                token.jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter())));
        return http.build();
    }



    /*
    CORS:- CORS stands for Cross-Origin Resource Sharing.
  1) It’s a security feature in browsers that controls how web pages can make requests to a different domain (origin) than the one that served the web page.
  2)This is a cross-origin request, because the frontend and backend have different ports, and browsers treat them as different origins.
  3)Without CORS, the browser blocks the request to protect you from malicious websites.

CORS is a set of HTTP headers that the backend can send to the browser to tell it:
“Yes, it’s okay for requests from origin http://localhost:4200 to access this resource.”
    */

    @Bean
    CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));   //only this will bew allowed
        config.setAllowedHeaders(Arrays.asList(
                ORIGIN, CONTENT_TYPE, ACCEPT, AUTHORIZATION  // You're allowing JavaScript to read specific response headers
        ));
        config.setAllowedMethods(
                Arrays.asList(
                        "GET", "POST", "PUT", "DELETE", "OPTIONS"
                )
        );
        source.registerCorsConfiguration("/**", config);  //for all resources /**
        return new org.springframework.web.filter.CorsFilter(source);
    }
}
