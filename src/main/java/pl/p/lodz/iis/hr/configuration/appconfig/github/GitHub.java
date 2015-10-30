package pl.p.lodz.iis.hr.configuration.appconfig.github;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

public final class GitHub {

    @JsonProperty("master")
    private final Master master;

    @JsonProperty("dummy")
    private final Dummy dummy;

    @JsonProperty("application")
    private final Application application;

    @JsonCreator
    public GitHub(@JsonProperty("master") Master master,
                  @JsonProperty("dummy") Dummy dummy,
                  @JsonProperty("application") Application application) {
        this.master = master;
        this.dummy = dummy;
        this.application = application;
    }

    public Master getMaster() {
        return master;
    }

    public Dummy getDummy() {
        return dummy;
    }

    public Application getApplication() {
        return application;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
