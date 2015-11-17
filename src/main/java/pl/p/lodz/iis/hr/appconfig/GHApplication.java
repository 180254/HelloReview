package pl.p.lodz.iis.hr.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class GHApplication {

    /* @formatter:off */
    @JsonProperty("appname")      private final String appName;
    @JsonProperty("clientid")     private final String clientID;
    @JsonProperty("clientsecret") private final String clientSecret;
    /* @formatter:on */

    @JsonCreator
    GHApplication(@JsonProperty("appname") String appName,
                  @JsonProperty("clientid") String clientID,
                  @JsonProperty("clientsecret") String clientSecret) {

        this.appName = appName;
        this.clientID = clientID;
        this.clientSecret = clientSecret;
    }

    public String getClientID() {
        return clientID;
    }

    public String getAppName() {
        return appName;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
