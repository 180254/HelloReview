package pl.p.lodz.iis.hr.configuration.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class GitHubDummy {

    /* @formatter:off */
    @JsonProperty("username")  private final String username;
    @JsonProperty("token")     private final String token;
    @JsonProperty("name")      private final String name;
    @JsonProperty("email")     private final String email;
    @JsonProperty("commitmsg") private final String commitMsg;
    /* @formatter:on */

    @JsonCreator
    GitHubDummy(@JsonProperty("username") String username,
                @JsonProperty("token") String token,
                @JsonProperty("name") String name,
                @JsonProperty("email") String email,
                @JsonProperty("commitmsg") String commitMsg) {

        this.username = username;
        this.token = token;
        this.name = name;
        this.email = email;
        this.commitMsg = commitMsg;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCommitMsg() {
        return commitMsg;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
