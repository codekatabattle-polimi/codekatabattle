package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.exceptions.OAuthException;
import it.polimi.codekatabattle.models.github.GHCheckTokenResponse;
import it.polimi.codekatabattle.models.github.GHUser;
import it.polimi.codekatabattle.models.oauth.OAuthAccessToken;
import it.polimi.codekatabattle.services.AuthService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String CLIENT_ID = "d65691ca61e893487d6b";
    private static final String CLIENT_SECRET = "933446e8338a421d40a1abcf713727859324ddbd";
    private static final String API_VERSION = "2022-11-28";

    @Override
    public OAuthAccessToken handleOAuthCallback(String code) throws OAuthException {
        OAuthAccessToken accessToken = RestClient.create().post()
            .uri("https://github.com/login/oauth/access_token")
            .accept(MediaType.APPLICATION_JSON)
            .body(Map.of("client_id", CLIENT_ID, "client_secret", CLIENT_SECRET, "code", code))
            .retrieve()
            .body(OAuthAccessToken.class);
        if (accessToken == null) {
            throw new OAuthException("Failed to obtain access token from GitHub");
        }

        return accessToken;
    }

    @Override
    public GHUser getUserInfo(String accessToken) throws OAuthException {
        GHCheckTokenResponse res = RestClient.create().post()
            .uri(String.format("https://api.github.com/applications/%s/token", CLIENT_ID))
            .header("Authorization", "Basic " + Base64.encodeBase64String(String.format("%s:%s", CLIENT_ID, CLIENT_SECRET).getBytes()))
            .header("X-GitHub-Api-Version", API_VERSION)
            .body(Map.of("access_token", accessToken.replace("Bearer ", "")))
            .retrieve()
            .body(GHCheckTokenResponse.class);
        if (res == null) {
            throw new OAuthException("Failed to get user info");
        }

        if (res.created_at.toInstant().isAfter(Instant.now())) {
            throw new OAuthException("Access token is not valid");
        }

        if (!Objects.equals(res.app.client_id, CLIENT_ID)) {
            throw new OAuthException("Access token does not come from CodeKataBattle application");
        }

        return res.user;
    }

}
