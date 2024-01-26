package it.polimi.codekatabattle.models.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OAuthConfig {

    private String clientId;

    private String clientSecret;

}
