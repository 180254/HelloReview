package pl.p.lodz.iis.hr.configuration.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class GitHub {

    @JsonProperty("master")
    private final GitHubMaster master;

    @JsonProperty("dummy")
    private final GitHubDummy dummy;

    @JsonProperty("application")
    private final GitHubApplication application;

    @JsonCreator
    public GitHub(@JsonProperty("master") GitHubMaster master,
                  @JsonProperty("dummy") GitHubDummy dummy,
                  @JsonProperty("application") GitHubApplication application) {
        this.master = master;
        this.dummy = dummy;
        this.application = application;
    }

    public GitHubMaster getMaster() {
        return master;
    }

    public GitHubDummy getDummy() {
        return dummy;
    }

    public GitHubApplication getApplication() {
        return application;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
