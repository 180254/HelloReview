package pl.p.lodz.iis.hr.configuration.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class GitHubApplication {

    /* @formatter:off */
    @JsonProperty("appname")      private final String appName;
    @JsonProperty("clientid")     private final String clientID;
    @JsonProperty("clientsecret") private final String clientSecret;
    /* @formatter:on */

    @JsonCreator
    GitHubApplication(@JsonProperty("appname") String appName,
                      @JsonProperty("clientid") String clientID,
                      @JsonProperty("clientsecret") String clientSecret) {

        this.appName = appName;
        this.clientID = clientID;
        this.clientSecret = clientSecret;
    }

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getAppName() {
        return appName;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
