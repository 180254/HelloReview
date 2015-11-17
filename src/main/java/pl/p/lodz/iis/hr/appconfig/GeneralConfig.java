package pl.p.lodz.iis.hr.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class GeneralConfig {

    /* @formatter:off */
    @JsonProperty("url")      private final String url;
    @JsonProperty("cachedir") private final String cacheDir;
    @JsonProperty("tempdir")  private final String tempDir;
    /* @formatter:on */

    @JsonCreator
    GeneralConfig(@JsonProperty("url") String url,
                  @JsonProperty("cachedir") String cacheDir,
                  @JsonProperty("tempdir") String tempDir) {

        this.url = url;
        this.cacheDir = cacheDir;
        this.tempDir = tempDir;
    }

    public String getUrl() {
        return url;
    }

    public String getCacheDir() {
        return cacheDir;
    }

    public String getTempDir() {
        return tempDir;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
