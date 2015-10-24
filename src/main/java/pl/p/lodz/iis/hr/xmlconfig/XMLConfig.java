package pl.p.lodz.iis.hr.xmlconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.apache.commons.lang3.builder.ToStringBuilder;
import pl.p.lodz.iis.hr.xmlconfig.github.GitHub;

@JacksonXmlRootElement(localName = "hello")
public final class XMLConfig {

    @JsonProperty("github")
    private final GitHub gitHub;

    @JsonCreator
    public XMLConfig(@JsonProperty("github") GitHub gitHub) {
        this.gitHub = gitHub;
    }

    public GitHub getGitHub() {
        return gitHub;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}