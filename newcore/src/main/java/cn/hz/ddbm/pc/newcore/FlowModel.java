package cn.hz.ddbm.pc.newcore;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import lombok.Data;

import java.util.HashSet;
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
        return 2;
    }

    public boolean isEnd(State state) {
        return false;
    }

    Profile profile;

    public void profile(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {
        Assert.notNull(profile, "profile is null");
        return profile;
    }

    public Boolean isRightState(S stateCode) {
        return allMasterStates.contains(stateCode);
    }
}
