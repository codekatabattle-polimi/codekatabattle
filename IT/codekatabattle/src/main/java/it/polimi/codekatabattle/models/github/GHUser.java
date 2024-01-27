package it.polimi.codekatabattle.models.github;

import lombok.Data;

@Data
public class GHUser {
    private int id;
    private String login;
    private String node_id;
    private String avatar_url;
    private String gravatar_id;
    private String url;
    private String html_url;
    private String repos_url;
    private String type;
    private boolean site_admin;
}
