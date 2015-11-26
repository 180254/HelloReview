package pl.p.lodz.iis.hr.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public final class GHConfig {

    /* @formatter:off */
    @JsonProperty("masters")     private final GHMasters masters;
    @JsonProperty("courserepos") private final GHCourseRepos courseRepos;
    @JsonProperty("dummy")       private final GHDummy dummy;
    @JsonProperty("application") private final GHApplication application;
    /* @formatter:on */

    @JsonCreator
    GHConfig(@JsonProperty("masters") GHMasters masters,
             @JsonProperty("courserepos") GHCourseRepos courseRepos,
             @JsonProperty("dummy") GHDummy dummy,
             @JsonProperty("application") GHApplication application) {

        this.masters = masters;
        this.courseRepos = courseRepos;
        this.dummy = dummy;
        this.application = application;
    }

    public GHMasters getMasters() {
        return masters;
    }

    public GHCourseRepos getCourseRepos() {
        return courseRepos;
    }

    public GHDummy getDummy() {
        return dummy;
    }

    public GHApplication getApplication() {
        return application;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("masters", masters)
                .add("courseRepos", courseRepos)
                .add("dummy", dummy)
                .add("application", application)
                .toString();
    }
}
