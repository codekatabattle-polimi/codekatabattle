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

    private static final String SWAGGER_CLIENT_ID = "d65691ca61e893487d6b";
    private static final String SWAGGER_CLIENT_SECRET = "933446e8338a421d40a1abcf713727859324ddbd";

    private static final String FRONTEND_CLIENT_ID = "0009d4a156ae3816fb0d";
    private static final String FRONTEND_CLIENT_SECRET = "d46e8b6fd4579f009335f1db8f164ded589b4e4a";

    private static final String API_VERSION = "2022-11-28";

    private final HashMap<AuthOrigin, OAuthConfig> oauthConfigMap = new HashMap<>() {{
        put(AuthOrigin.SWAGGER, new OAuthConfig(SWAGGER_CLIENT_ID, SWAGGER_CLIENT_SECRET));
        put(AuthOrigin.FRONTEND, new OAuthConfig(FRONTEND_CLIENT_ID, FRONTEND_CLIENT_SECRET));
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
    public it.polimi.codekatabattle.models.github.GHUser getUserInfo(String accessToken, AuthOrigin authOrigin) throws OAuthException {
        OAuthConfig oauthConfig = getOAuthConfig(authOrigin);

        ResponseEntity<GHCheckTokenResponse> response = RestClient.create().post()
            .uri(String.format("https://api.github.com/applications/%s/token", oauthConfig.getClientId()))
            .header("Authorization", "Basic " + Base64.encodeBase64String(String.format("%s:%s", oauthConfig.getClientId(), oauthConfig.getClientSecret()).getBytes()))
            .header("X-GitHub-Api-Version", API_VERSION)
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

        if (!Objects.equals(body.app.client_id, SWAGGER_CLIENT_ID) && !Objects.equals(body.app.client_id, FRONTEND_CLIENT_ID)) {
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

    @Override
    public void checkAccessToken(String accessToken, String origin) throws OAuthException {
        this.getUserInfo(accessToken, this.getAuthOriginFromOriginHeader(origin));
    }

}
