package it.polimi.codekatabattle.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.polimi.codekatabattle.exceptions.OAuthException;
import it.polimi.codekatabattle.models.github.GHUser;
import it.polimi.codekatabattle.models.oauth.OAuthAccessToken;
import it.polimi.codekatabattle.services.AuthService;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "Auth", description = "Endpoints related to authentication")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(
        path = "/callback",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "OAuth callback",
        description = "Get access token by providing code from GitHub"
    )
    public ResponseEntity<OAuthAccessToken> callback(
        @RequestBody MultiValueMap<String, String> formData,
        @Parameter(hidden = true) @RequestHeader(value = "Origin", required = false) String origin
    ) throws OAuthException {
        OAuthAccessToken accessToken = authService.handleOAuthCallback(formData.getFirst("code"), authService.getAuthOriginFromOriginHeader(origin));
        return ResponseEntity.ok().body(accessToken);
    }

    @GetMapping("/me")
    @Operation(
        summary = "Get logged-in user info",
        description = "Get user info by providing access token",
        security = @SecurityRequirement(name = "github")
    )
    public ResponseEntity<GHUser> me(
        @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
        @Parameter(hidden = true) @RequestHeader(value = "Origin", required = false) String origin
    ) throws OAuthException {
        return ResponseEntity.ok().body(authService.getUserInfo(accessToken, authService.getAuthOriginFromOriginHeader(origin)));
    }

    @GetMapping("/user")
    @Operation(
        summary = "Get generic user info",
        description = "Get user info by providing access token and username",
        security = @SecurityRequirement(name = "github")
    )
    public ResponseEntity<GHUser> getUserInfo(
        @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
        @Parameter(description = "Username", required = true) @RequestParam("username") String username
    ) throws IOException {
        GitHub github = new GitHubBuilder().withOAuthToken(accessToken.replace("Bearer ", "")).build();
        return ResponseEntity.ok().body(authService.getUserInfo(github, username));
    }

}
