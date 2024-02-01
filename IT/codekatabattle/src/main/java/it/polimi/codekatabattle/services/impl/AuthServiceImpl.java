package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.exceptions.OAuthException;
import it.polimi.codekatabattle.models.github.GHCheckTokenResponse;
import it.polimi.codekatabattle.models.github.GHUser;
import it.polimi.codekatabattle.models.oauth.AuthOrigin;
import it.polimi.codekatabattle.models.oauth.OAuthAccessToken;
import it.polimi.codekatabattle.models.oauth.OAuthConfig;
import it.polimi.codekatabattle.services.AuthService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${github.api.version}")
    private String githubApiVersion = "2022-11-28";

    @Value("${github.swagger.client-id}")
    private String swaggerClientId;

    @Value("${github.swagger.client-secret}")
    private String swaggerClientSecret;

    @Value("${github.frontend.client-id}")
    private String frontendClientId;

    @Value("${github.frontend.client-secret}")
    private String frontendClientSecret;

    @Value("${ckb.test}")
    private Boolean isTest;

    private final HashMap<AuthOrigin, OAuthConfig> oauthConfigMap = new HashMap<>() {{
        put(AuthOrigin.SWAGGER, new OAuthConfig(swaggerClientId, swaggerClientSecret));
        put(AuthOrigin.FRONTEND, new OAuthConfig(frontendClientId, frontendClientSecret));
    }};

    private OAuthConfig getOAuthConfig(AuthOrigin authOrigin) {
        return oauthConfigMap.get(authOrigin);
    }

    @Override
    public OAuthAccessToken handleOAuthCallback(String code, AuthOrigin authOrigin) throws OAuthException {
        OAuthConfig oauthConfig = getOAuthConfig(authOrigin);

        ResponseEntity<OAuthAccessToken> response = RestClient.create().post()
            .uri("https://github.com/login/oauth/access_token")
            .accept(MediaType.APPLICATION_JSON)
            .body(Map.of("client_id", oauthConfig.getClientId(), "client_secret", oauthConfig.getClientSecret(), "code", code))
            .retrieve()
            .toEntity(OAuthAccessToken.class);
        if (response.getStatusCode().isError()) {
            throw new OAuthException("Failed to obtain access token from GitHub");
        }
        if (response.getBody() == null || response.getBody().access_token == null) {
            throw new OAuthException("Invalid response body");
        }

        return response.getBody();
    }

    @Override
    public GHUser getUserInfo(String accessToken, AuthOrigin authOrigin) throws OAuthException {
        if (isTest) {
            // While inside a test environment, we use the PAT to authenticate with GitHub instead of the GitHub application.
            // This requires a different API endpoint call.
            try {
                GitHub github = new GitHubBuilder().withOAuthToken(accessToken.replace("Bearer ", "")).build();
                return GHUser.fromSDKUser(github.getMyself());
            } catch (IOException ex) {
                throw new OAuthException(ex.getMessage());
            }
        }

        OAuthConfig oauthConfig = getOAuthConfig(authOrigin);

        ResponseEntity<GHCheckTokenResponse> response = RestClient.create().post()
            .uri(String.format("https://api.github.com/applications/%s/token", oauthConfig.getClientId()))
            .header("Authorization", "Basic " + Base64.encodeBase64String(String.format("%s:%s", oauthConfig.getClientId(), oauthConfig.getClientSecret()).getBytes()))
            .header("X-GitHub-Api-Version", githubApiVersion)
            .body(Map.of("access_token", accessToken.replace("Bearer ", "")))
            .retrieve()
            .toEntity(GHCheckTokenResponse.class);
        if (response.getStatusCode().isError()) {
            throw new OAuthException("Failed to get user info");
        }

        GHCheckTokenResponse body = response.getBody();
        if (body == null) {
            throw new OAuthException("Invalid response body");
        }

        if (!Objects.equals(body.app.client_id, swaggerClientId) && !Objects.equals(body.app.client_id, frontendClientId)) {
            throw new OAuthException("Access token does not come from a valid CodeKataBattle application");
        }

        return body.user;
    }

    @Override
    public AuthOrigin getAuthOriginFromOriginHeader(String origin) {
        if (origin == null) {
            return AuthOrigin.SWAGGER;
        }
        if (origin.contains("localhost:5173")) {
            return AuthOrigin.FRONTEND;
        }

        return AuthOrigin.SWAGGER;
    }

    @Override
    public GHUser getUserInfo(GitHub github, String username) throws IOException {
        return GHUser.fromSDKUser(github.getUser(username));
    }

}
