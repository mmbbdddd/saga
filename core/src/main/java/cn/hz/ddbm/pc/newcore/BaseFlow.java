package cn.hz.ddbm.pc.newcore;

import cn.hz.ddbm.pc.newcore.config.Coast;
import lombok.Data;

import java.util.Objects;

@Data
public abstract class BaseFlow<S extends State> {
   protected String  name;
   protected Profile profile;

    public Integer getRetry(S state) {
        return getProfile().getStateAttrs(state).getRetry();
    }

    public void profile(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {
        String runMode = System.getProperty(Coast.RUN_MODE);
        if (Objects.equals(runMode, Coast.RUN_MODE_CHAOS)) {
            return profile == null ? Profile.chaosOf() : profile;
        } else {
            return profile == null ? Profile.of() : profile;
        }
    }
}
