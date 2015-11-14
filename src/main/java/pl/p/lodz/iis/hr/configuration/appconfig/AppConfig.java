package pl.p.lodz.iis.hr.configuration.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Immutable;

@JacksonXmlRootElement(localName = "hello")
@Immutable
public final class AppConfig {

    @JsonProperty("generalConfig")
    private final GeneralConfig generalConfig;

    @JsonProperty("github")
    private final GitHubConfig gitHubConfig;

    @JsonCreator
    AppConfig(@JsonProperty("general") GeneralConfig generalConfig,
              @JsonProperty("github") GitHubConfig gitHubConfig) {
        this.generalConfig = generalConfig;
        this.gitHubConfig = gitHubConfig;
    }

    public GeneralConfig getGeneralConfig() {
        return generalConfig;
    }

    public GitHubConfig getGitHubConfig() {
        return gitHubConfig;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
