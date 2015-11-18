package pl.p.lodz.iis.hr.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GHCourseRepos {

    @JsonProperty("username")
    private final List<String> userNames;

    @JsonCreator
    GHCourseRepos(@JsonProperty("username") List<String> userNames) {

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