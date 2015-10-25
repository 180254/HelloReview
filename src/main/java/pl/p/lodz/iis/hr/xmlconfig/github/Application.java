package pl.p.lodz.iis.hr.xmlconfig.github;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

public final class Application {

    @JsonProperty("clientID")
    private final String clientID;

    @JsonProperty("clientSecret")
    private final String clientSecret;

    @JsonCreator
    public Application(@JsonProperty("clientID") String clientID,
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
        return ToStringBuilder.reflectionToString(this);
    }
}