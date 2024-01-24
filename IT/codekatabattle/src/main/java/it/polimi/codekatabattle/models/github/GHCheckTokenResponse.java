package it.polimi.codekatabattle.models.github;

import java.util.Date;
import java.util.List;

public class GHCheckTokenResponse {
    public int id;
    public String url;
    public List<String> scopes;
    public String token;
    public String token_last_eight;
    public String hashed_token;
    public GHApp app;
    public String note;
    public String note_url;
    public Date updated_at;
    public Date created_at;
    public String fingerprint;
    public Date expires_at;
    public GHUser user;
}
