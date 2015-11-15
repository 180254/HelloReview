package pl.p.lodz.iis.hr.configuration.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class GitHubApplication {

    /* @formatter:off */
    @JsonProperty("clientID")     private final String clientID;
    @JsonProperty("clientSecret") private final String clientSecret;
    /* @formatter:on */

    @JsonCreator
    GitHubApplication(@JsonProperty("clientID") String clientID,
                      @JsonProperty("clientSecret") String clientSecret) {

        this.clientID = clientID;
        this.clientSecret = clientSecret;
    }

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
