package it.polimi.codekatabattle.models.github;

import lombok.Data;

import java.io.IOException;

@Data
public class GHUser {
    private long id;
    private String login;
    private String node_id;
    private String avatar_url;
    private String gravatar_id;
    private String url;
    private String html_url;
    private String repos_url;
    private String type;
    private boolean site_admin;

    public static GHUser fromSDKUser(org.kohsuke.github.GHUser user) throws IOException {
        GHUser ghUser = new GHUser();
        ghUser.id = user.getId();
        ghUser.login = user.getLogin();
        ghUser.node_id = user.getNodeId();
        ghUser.avatar_url = user.getAvatarUrl();
        ghUser.gravatar_id = user.getGravatarId();
        ghUser.url = user.getUrl().toString();
        ghUser.html_url = user.getHtmlUrl().toString();
        ghUser.repos_url = null;
        ghUser.type = null;
        ghUser.site_admin = user.isSiteAdmin();
        return ghUser;
    }
}
