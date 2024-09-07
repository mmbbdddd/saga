package cn.hz.ddbm.pc.newcore;

import java.util.HashSet;
import java.util.Set;

public enum OffsetState {
    init, task, failover, retry, su,fail, manual, pause, cancel;

    public static Set<OffsetState> runnables() {
        Set<OffsetState> sets = new HashSet<>();
        sets.add(task);
        sets.add(failover);
        sets.add(retry);
        return sets;
    }

    public static Set<OffsetState> ends() {
        Set<OffsetState> sets = new HashSet<>();
        sets.add(su);
        sets.add(fail);
        sets.add(cancel);
        return sets;
    }

    public static Set<OffsetState> pauseds() {
        Set<OffsetState> sets = new HashSet<>();
        sets.add(manual);
        sets.add(pause);
        return sets;
    }

    public Boolean isRunnable() {
        return runnables().contains(this);
    }

    public Boolean isPaused() {
        return pauseds().contains(this);
    }

    public Boolean isEnd() {
        return ends().contains(this);
    }

}
