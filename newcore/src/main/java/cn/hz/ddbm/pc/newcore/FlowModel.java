package cn.hz.ddbm.pc.newcore;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public abstract class FlowModel<S extends State> {
    String name;
    S      init;
    Set<S> ends;
    Set<S> tasks;
    Set<S> allMasterStates;

    public FlowModel(String name, S init, Set<S> ends, Set<S> tasks) {
        Assert.notNull(name, "name is null");
        Assert.notNull(init, "init is null");
        Assert.notNull(ends, "ends is null");
        Assert.notNull(tasks, "tasks is null");
        this.name            = name;
        this.init            = init;
        this.ends            = ends;
        this.tasks           = tasks;
        this.allMasterStates = new HashSet<>();
        this.allMasterStates.add(init);
        this.allMasterStates.addAll(ends);
        this.allMasterStates.addAll(tasks);
    }

    public Integer getRetry(S state) {
        return getProfile().getStateAttrs(state).getRetry();
    }

    public boolean isEnd(S state) {
        return ends.contains(state);
    }

    Profile profile;

    public void profile(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {
        String runMode = System.getProperty(Coast.RUN_MODE);
        if(Objects.equals(runMode,Coast.RUN_MODE_CHAOS)){
            return Profile.chaosOf();
        }else {
            return profile == null ? Profile.of() : profile;
        }
    }

    public Boolean isRightState(S stateCode) {
        return allMasterStates.contains(stateCode);
    }
}
