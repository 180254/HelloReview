package pl.p.lodz.iis.hr.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public final class GeneralConfig {

    /* @formatter:off */
    @JsonProperty("cachedir") private final String cacheDir;
    @JsonProperty("tempdir")  private final String tempDir;
    /* @formatter:on */

    @JsonCreator
    GeneralConfig(@JsonProperty("cachedir") String cacheDir,
                  @JsonProperty("tempdir") String tempDir) {

        this.cacheDir = cacheDir;
        this.tempDir = tempDir;
    }
    public String getCacheDir() {
        return cacheDir;
    }

    public String getTempDir() {
        return tempDir;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("cacheDir", cacheDir)
                .add("tempDir", tempDir)
                .toString();
    }
}
