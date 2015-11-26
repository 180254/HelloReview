package pl.p.lodz.iis.hr.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public final class GHDummy {

    /* @formatter:off */
    @JsonProperty("token")     private final String token;
    @JsonProperty("commitmsg") private final String commitMsg;
    /* @formatter:on */

    @JsonCreator
    GHDummy(@JsonProperty("token") String token,
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
        return MoreObjects.toStringHelper(this)
                .add("token", token)
                .add("commitMsg", commitMsg)
                .toString();
    }
}
