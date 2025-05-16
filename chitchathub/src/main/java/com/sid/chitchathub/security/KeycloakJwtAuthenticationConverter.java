package com.sid.chitchathub.security;

import lombok.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeycloakJwtAuthenticationConverter implements org.springframework.core.convert.converter.Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {
        return new JwtAuthenticationToken(source,
                //This will convert the realm roles ,its by default used by spring
                Stream.concat(new JwtGrantedAuthoritiesConverter().convert(source).stream(),
                        //Here we are extracting client roles from jwt and setting in JwtAuthenticationToken that represents authenticated user
                        extractResourceRoles(source).stream()).collect(Collectors.toSet()));
    }


    //"resource_access": {
    //  "account": {
    //    "roles": ["manage-account", "view-profile"]
    //  },
    //  "chat-app": {
    //    "roles": ["chat_user"]
    //  }
    //}


    //A public client automatically created when a new realm is created.
    //Used for user self-service (managing their own account).

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        var resourceAccess = new HashMap<>(jwt.getClaim("resource_access"));

        var eternal = (Map<String, List<String>>) resourceAccess.get("account");

        var roles = eternal.get("roles");

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.replace("-", "_")))
                .collect(Collectors.toSet());
    }

    /*
   1) CORS = browser security check for cross-origin HTTP requests
   2) In Spring Boot, use @CrossOrigin or a CORS config bean to allow frontend requests
   3) Required when frontend and backend are on different domains/ports
   */



}
