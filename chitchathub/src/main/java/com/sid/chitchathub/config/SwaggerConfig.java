package com.sid.chitchathub.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
//This annotation tells Swagger/OpenAPI how your secured API is protected, so Swagger UI can allow users to authenticate directly from the UI using OAuth2.
@SecurityScheme(
        name = "keycloak",                  // 🔹 Name to be referenced in @SecurityRequirement
        type = SecuritySchemeType.OAUTH2,  // 🔹 You are using OAuth2 protocol
        scheme = "bearer",                 // 🔹 It’s a Bearer token-based auth
        bearerFormat = "JWT",              // 🔹 The format of the token is JWT
        in = SecuritySchemeIn.HEADER,      // 🔹 Token is passed in the HTTP header
        flows = @OAuthFlows(
                password = @OAuthFlow(
                        authorizationUrl = "http://localhost:9090/realms/chit-chat-hub/protocol/openid-connect/auth",
                        tokenUrl = "http://localhost:9090/realms/chit-chat-hub/protocol/openid-connect/token"  //swagger will fetch token from there
                )
        )
)
public class SwaggerConfig {
}
