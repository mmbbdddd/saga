package cn.hz.ddbm.pc.core;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.exception.ActionException;
import cn.hz.ddbm.pc.core.exception.IdempotentException;
import cn.hz.ddbm.pc.core.exception.StatusException;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @param <S> 状态枚举
 */
@Getter
public class Fsm {
    //状态机
    final   String          name;
    //状态机描述
    final   String          descr;
    //初始节点
    final   State           init;
    //状态机节点对象。
    final   Set<State>      tasks;
    final   Set<State>      ends;
    //运行时配置参数
    final   Profile         profile;
    //状态机事件定义表
    private Set<Transition> eventTable;

    public Fsm(String name, String descr, State init, Set<State> tasks, Set<State> ends, Profile profile) {
        Assert.notNull(name, "flow.name is null");
        Assert.notNull(init, "init is null");
        Assert.notNull(tasks, "tasks is null");
        Assert.notNull(ends, "ends is null");
        Assert.notNull(profile, "profile is null");
        this.name       = name;
        this.descr      = descr;
        this.profile    = profile;
        this.ends       = ends;
        this.init       = init;
        this.tasks      = tasks;
        this.eventTable = new HashSet<>();
    }

    public void validate() {
        Assert.notNull(eventTable, "eventTable is null");
        Assert.isTrue(eventTable.isEmpty(), "eventTable is empty");
    }

    /**
     * 定义流程参数
     *
     * @param name
     * @param profile
     * @return
     */
    public static   Fsm  of(String name, String descr, State init, Set<State> tasks, Set<State> ends, Profile  profile) {
        return new Fsm(name, descr, init, tasks, ends, profile);
    }

    /**
     * 定义开发环境流程参数
     *
     * @param name
     * @return
     */
    public static   Fsm  devOf(String name, String descr, State init, Set<State> tasks, Set<State> ends) {
        List<String> plugins = new ArrayList<>();
        plugins.add(Coasts.PLUGIN_DIGEST_LOG);
        plugins.add(Coasts.PLUGIN_ERROR_LOG);
        Fsm  flow = new Fsm(name, descr, init, tasks, ends, Profile.devOf());
        return flow;
    }


    public <T> void execute(FsmContext ctx) throws FsmEndException, StatusException, ActionException, IdempotentException {
        State state = ctx.getState();
        if (!ctx.getFlow().isRunnableState(state)) {
            throw new FsmEndException();
        }
        Transition  transition = findEventTable(state, ctx.getEvent());
        Assert.notNull(transition, String.format("找不到事件处理器%s@%s", ctx.getEvent(), ctx.getState()));
        ctx.initTransitionAndProcessor(transition);
        ctx.getProcessor().execute(ctx);
    }

    public Transition findEventTable(State node, String event) {
        return eventTable.stream().filter(r -> Objects.equals(r.getFrom(), node) && Objects.equals(r.getEvent(), event)).findFirst().orElse(null);
    }

    public void buildEventTable(List<Transition > transitions) {
        if (this.eventTable.isEmpty()) {
            eventTable.addAll(transitions);
        } else {
            throw new RuntimeException("事件表不能重复定义");
        }
    }

    public boolean isRunnableState(State node) {
        return node.equals(init) || tasks.contains(node);
    }


    public State startStep() {
        return init;
    }


    public Set<String> nodeNames() {
        return tasks.stream().map(t->t.code().toString()).collect(Collectors.toSet());
    }




    @Override
    public String toString() {
        return "{" + "name:'" + name + '\'' + ", descr:'" + descr + '\'' + ", init:" + init + ",nodes:" + tasks + ", fsmTable:" + Arrays.toString(eventTable.toArray(new Transition[eventTable.size()])) + '}';
    }


}
