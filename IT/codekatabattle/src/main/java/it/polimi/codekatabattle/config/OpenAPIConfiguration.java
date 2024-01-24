package it.polimi.codekatabattle.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
    info = @Info(
        title = "CodeKataBattle API",
        description = "CodeKataBattle API Documentation"
    )
)
@SecurityScheme(
    name = "github",
    type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(authorizationCode = @OAuthFlow(
        authorizationUrl = "https://github.com/login/oauth/authorize",
        tokenUrl = "http://localhost:8000/auth/callback",
        scopes = @OAuthScope(name = "read:user", description = "Read user profile")
    ))
)
public class OpenAPIConfiguration {
}
