package pl.p.lodz.iis.hr.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.MoreObjects;

@JacksonXmlRootElement(localName = "hello")
public final class AppConfig {

    /* @formatter:off */
    @JsonProperty("generalConfig") private final GeneralConfig generalConfig;
    @JsonProperty("github")        private final GHConfig gitHubConfig;
    /* @formatter:on */

    @JsonCreator
    AppConfig(@JsonProperty("general") GeneralConfig generalConfig,
              @JsonProperty("github") GHConfig gitHubConfig) {

        this.generalConfig = generalConfig;
        this.gitHubConfig = gitHubConfig;
    }

    public GeneralConfig getGeneralConfig() {
        return generalConfig;
    }

    public GHConfig getGitHubConfig() {
        return gitHubConfig;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("generalConfig", generalConfig)
                .add("gitHubConfig", gitHubConfig)
                .toString();
    }
}
