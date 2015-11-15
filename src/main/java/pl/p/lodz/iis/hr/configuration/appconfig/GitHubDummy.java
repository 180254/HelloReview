package pl.p.lodz.iis.hr.configuration.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class GitHubDummy {

    /* @formatter:off */
    @JsonProperty("name")      private final String name;
    @JsonProperty("email")     private final String email;
    @JsonProperty("username")  private final String username;
    @JsonProperty("password")  private final String password;
    @JsonProperty("commitmsg") private final String commitMsg;
    /* @formatter:on */

    @JsonCreator
    GitHubDummy(@JsonProperty("name") String name,
                @JsonProperty("email") String email,
                @JsonProperty("username") String username,
                @JsonProperty("password") String password,
                @JsonProperty("commitmsg") String commitMsg) {

        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.commitMsg = commitMsg;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCommitMsg() {
        return commitMsg;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
