package cn.hz.ddbm.pc.newcore;

import cn.hutool.core.lang.Assert;
import lombok.Data;

import java.util.Set;

@Data
public abstract class FlowModel<S extends State> {
    String name;
    S      init;
    Set<S> ends;
    Set<S> tasks;

    public FlowModel(String name, S init, Set<S> ends, Set<S> tasks) {
        Assert.notNull(name, "name is null");
        Assert.notNull(init, "init is null");
        Assert.notNull(ends, "ends is null");
        Assert.notNull(tasks, "tasks is null");
        this.name  = name;
        this.init  = init;
        this.ends  = ends;
        this.tasks = tasks;
    }

    public Integer getRetry(S state) {
        return 5;
    }

}
