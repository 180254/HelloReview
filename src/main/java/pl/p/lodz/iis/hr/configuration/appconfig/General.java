package pl.p.lodz.iis.hr.configuration.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Immutable;

@Immutable
public final class General {

    @JsonProperty("url")
    private final String url;

    @JsonProperty("cachedir")
    private final String cacheDir;

    @JsonCreator
    General(@JsonProperty("url") String url,
            @JsonProperty("cachedir") String cacheDir) {

        this.url = url;
        this.cacheDir = cacheDir;
    }

    public String getUrl() {
        return url;
    }

    public String getCacheDir() {
        return cacheDir;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
