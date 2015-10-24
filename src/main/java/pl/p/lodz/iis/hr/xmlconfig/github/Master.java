package pl.p.lodz.iis.hr.xmlconfig.github;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

public final class Master {

    @JsonProperty("username")
    private final String username;

    @JsonCreator
    public Master(@JsonProperty("username") String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}