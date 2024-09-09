package cn.hz.ddbm.pc.newcore;

import cn.hz.ddbm.pc.newcore.config.Coast;
import lombok.Data;

import java.util.Objects;
import java.util.Set;

@Data
public abstract class FlowModel<S extends State> {
    protected String name;
    protected S      init;
    protected Set<S> ends;
    protected Set<S> tasks;
    protected Set<S> allStates;


    public Integer getRetry(S state) {
        return getProfile().getStateAttrs(state).getRetry();
    }

    public abstract boolean isEnd(S state);

    public Boolean isState(S stateCode) {
        return allStates.contains(stateCode);
    }

    Profile profile;

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
