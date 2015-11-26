package pl.p.lodz.iis.hr.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

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
        return MoreObjects.toStringHelper(this)
                .add("appName", appName)
                .add("clientID", clientID)
                .add("clientSecret", clientSecret)
                .toString();
    }
}
