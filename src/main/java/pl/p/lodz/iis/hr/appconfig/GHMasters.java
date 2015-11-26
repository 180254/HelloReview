package pl.p.lodz.iis.hr.appconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GHMasters {

    @JsonProperty("username")
    private final List<String> userNames;

    @JsonCreator
    GHMasters(@JsonProperty("username") List<String> userNames) {

        this.userNames = new ArrayList<>(userNames);
    }

    public List<String> getUserNames() {
        return Collections.unmodifiableList(userNames);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("userNames", userNames)
                .toString();
    }
}
