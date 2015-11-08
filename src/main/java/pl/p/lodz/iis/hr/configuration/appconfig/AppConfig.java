package pl.p.lodz.iis.hr.configuration.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@JacksonXmlRootElement(localName = "hello")
public final class AppConfig {

    @JsonProperty("general")
    private final General general;

    @JsonProperty("github")
    private final GitHub gitHub;

    @JsonCreator
    AppConfig(@JsonProperty("general") General general,
              @JsonProperty("github") GitHub gitHub) {
        this.general = general;
        this.gitHub = gitHub;
    }

    public General getGeneral() {
        return general;
    }

    public GitHub getGitHub() {
        return gitHub;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
