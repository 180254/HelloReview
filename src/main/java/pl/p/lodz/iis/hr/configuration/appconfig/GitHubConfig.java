package pl.p.lodz.iis.hr.configuration.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class GitHubConfig {

    /* @formatter:off */
    @JsonProperty("masters")     private final GitHubMasters masters;
    @JsonProperty("courserepos") private final GitHubCourseRepos courseRepos;
    @JsonProperty("dummy")       private final GitHubDummy dummy;
    @JsonProperty("application") private final GitHubApplication application;
    /* @formatter:on */

    @JsonCreator
    GitHubConfig(@JsonProperty("masters") GitHubMasters masters,
                 @JsonProperty("courserepos") GitHubCourseRepos courseRepos,
                 @JsonProperty("dummy") GitHubDummy dummy,
                 @JsonProperty("application") GitHubApplication application) {

        this.masters = masters;
        this.courseRepos = courseRepos;
        this.dummy = dummy;
        this.application = application;
    }

    public GitHubMasters getMasters() {
        return masters;
    }

    public GitHubCourseRepos getCourseRepos() {
        return courseRepos;
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
