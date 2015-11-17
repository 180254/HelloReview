package pl.p.lodz.iis.hr.configuration.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class GitHubDummy {

    /* @formatter:off */
    @JsonProperty("token")     private final String token;
    @JsonProperty("commitmsg") private final String commitMsg;
    /* @formatter:on */

    @JsonCreator
    GitHubDummy(@JsonProperty("username") String username,
                @JsonProperty("token") String token,
                @JsonProperty("name") String name,
                @JsonProperty("email") String email,
                @JsonProperty("commitmsg") String commitMsg) {

        this.token = token;
        this.commitMsg = commitMsg;
    }


    public String getToken() {
        return token;
    }

    public String getCommitMsg() {
        return commitMsg;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
