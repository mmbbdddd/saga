package cn.hz.ddbm.pc.core;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.exception.ActionException;
import cn.hz.ddbm.pc.core.exception.wrap.StatusException;
import lombok.Data;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @param <S> 状态枚举
 */
@Getter
public class Fsm<S extends Enum<S>> {
    //状态机
    final String          name;
    //状态机描述
    final String          descr;
    //初始节点
    final S               init;
    //运行时配置参数
    final Profile<S>      profile;
    //状态机节点对象。
    final Map<S, Node<S>> tasks;
    final Set<S>          ends;
    //状态机事件定义表
    EventTable<S> eventTable;

    public Fsm(String name, String descr, S init, Set<S> tasks, Set<S> ends, Profile<S> profile) {
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
        this.tasks      = tasks.stream().collect(Collectors.toMap(t -> t, t -> new Node<>(t, profile)));
        this.eventTable = new EventTable<>();
    }


    /**
     * 定义流程参数
     *
     * @param name
     * @param profile
     * @return
     */
    public static <S extends Enum<S>> Fsm<S> of(String name, String descr, S init, Set<S> tasks, Set<S> ends, Profile<S> profile) {
        return new Fsm<>(name, descr, init, tasks, ends, profile);
    }

    /**
     * 定义开发环境流程参数
     *
     * @param name
     * @return
     */
    public static <S extends Enum<S>> Fsm<S> devOf(String name, String descr, S init, Set<S> tasks, Set<S> ends) {
        List<String> plugins = new ArrayList<>();
        plugins.add(Coasts.PLUGIN_DIGEST_LOG);
        plugins.add(Coasts.PLUGIN_ERROR_LOG);
        Fsm<S> flow = new Fsm<>(name, descr, init, tasks, ends, Profile.devOf());
        return flow;
    }


    public <T> void execute(FsmContext<S, ?> ctx) throws FsmEndException, StatusException, ActionException {
        S state = ctx.getState();
        if (!ctx.getFlow().isRunnable(state)) {
            throw new FsmEndException();
        }
        Transition<S> transition = eventTable.find(state, ctx.getEvent());
        Assert.notNull(transition, String.format("找不到事件处理器%s@%s", ctx.getEvent(), ctx.getState()));
        ctx.setTransition(transition);
        transition.execute(ctx);
    }

    public boolean isRunnable(S node) {
        return node.equals(init) || tasks.containsKey(node);
    }


    public S startStep() {
        return init;
    }


    public Set<String> nodeNames() {
        return tasks.keySet().stream().map(Enum::name).collect(Collectors.toSet());
    }


    public Node<S> getNode(S state) {
        return tasks.get(state);
    }


    @Override
    public String toString() {
        return "{" + "name:'" + name + '\'' + ", descr:'" + descr + '\'' + ", init:" + init + ",nodes:" + tasks + ", fsmTable:" + Arrays.toString(eventTable.records.toArray(new Transition[eventTable.records.size()])) + '}';
    }


    @Data
    public static class EventTable<S extends Enum<S>> {
        private Set<Transition<S>> records;

        public EventTable() {
            this.records = new HashSet<>();
        }

        public Transition<S> find(S node, String event) {
            return records.stream().filter(r -> Objects.equals(r.getFrom(), node) && Objects.equals(r.getEvent(), event)).findFirst().orElse(null);
        }


        /**
         * 暴露给用户的接口
         * 内部被拆分成1+N
         * 1，node=>event==>nodeOf(action,router)
         * 2,nodeOf(action,router)==>routerResultEvent==>routerResultNode
         * 参见onInner
         */
        public EventTable<S> to(S from, String event, String toAction, S to) {
            this.records.add(Transition.toOf(from, event, toAction, to));
            this.records.add(Transition.toOf(from, Coasts.EVENT_DEFAULT, toAction, to));
            return this;
        }

        public EventTable<S> query(S from, String actionDsl) {
            this.records.add(Transition.queryOf(from, Coasts.EVENT_DEFAULT, actionDsl));
            return this;
        }

        public EventTable<S> saga(S from, Set<S> conditions, S failover, String actionDsl) {
            this.records.add(Transition.sagaOf(from, Coasts.EVENT_DEFAULT, conditions, failover, actionDsl));
            this.records.add(Transition.queryOf(failover, Coasts.EVENT_DEFAULT, actionDsl));
            return this;
        }

        public EventTable<S> saga(S from, String event, Set<S> conditions, S failover, String actionDsl) {
            this.records.add(Transition.sagaOf(from, event, conditions, failover, actionDsl));
            this.records.add(Transition.queryOf(failover, Coasts.EVENT_DEFAULT, actionDsl));
            return this;
        }


        @Override
        public String toString() {
            return Arrays.toString(records.toArray());
        }
    }

}
