package it.polimi.codekatabattle.services;

import it.polimi.codekatabattle.exceptions.OAuthException;
import it.polimi.codekatabattle.models.github.GHUser;
import it.polimi.codekatabattle.models.oauth.AuthOrigin;
import it.polimi.codekatabattle.models.oauth.OAuthAccessToken;

public interface AuthService {

    OAuthAccessToken handleOAuthCallback(String code, AuthOrigin origin) throws OAuthException;

    GHUser getUserInfo(String accessToken, AuthOrigin authOrigin) throws OAuthException;

    AuthOrigin getAuthOriginFromOriginHeader(String origin);
}
