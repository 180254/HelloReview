package pl.p.lodz.iis.hr.configuration.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Immutable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Immutable
public final class GitHubMasters {

    @JsonProperty("username")
    private final List<String> userNames;

    @JsonCreator
    GitHubMasters(@JsonProperty("username") List<String> userNames) {
        this.userNames = new ArrayList<>(userNames);
    }

    public List<String> getUserNames() {
        return Collections.unmodifiableList(userNames);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
