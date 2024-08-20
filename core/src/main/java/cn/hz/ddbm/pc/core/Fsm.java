package cn.hz.ddbm.pc.core;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.enums.FlowStatus;
import cn.hz.ddbm.pc.core.exception.wrap.ActionException;
import cn.hz.ddbm.pc.core.exception.wrap.StatusException;
import cn.hz.ddbm.pc.core.processor.RouterProcessor;
import cn.hz.ddbm.pc.core.processor.SagaProcessor;
import cn.hz.ddbm.pc.core.processor.ToProcessor;
import cn.hz.ddbm.pc.core.utils.InfraUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
    final Map<S, Node<S>> nodes;
    //状态机插件
    @Setter
    List<Plugin> plugins;
    //状态机事件定义表
    EventTable<S> eventTable;

    public Fsm(String name, String descr, Map<S, FlowStatus> nodesType, Profile<S> profile) {
        Assert.notNull(name, "flow.name is null");
        Assert.notNull(nodesType, "nodesType is null");
        Assert.notNull(profile, "profile is null");
        this.name       = name;
        this.descr      = descr;
        this.profile    = profile;
        this.nodes      = nodesType.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, t -> new Node<>(t.getKey(), t.getValue(), profile)));
        this.init       = this.nodes.values().stream().filter(t -> t.type.equals(FlowStatus.INIT)).findFirst().get().name;
        this.eventTable = new EventTable<>();
        this.plugins    = new ArrayList<>();
    }


    /**
     * 定义流程参数
     *
     * @param name
     * @param profile
     * @return
     */
    public static <S extends Enum<S>> Fsm<S> of(String name, String descr, Map<S, FlowStatus> nodesType, Profile profile) {
        return new Fsm<>(name, descr, nodesType, profile);
    }

    /**
     * 定义开发环境流程参数
     *
     * @param name
     * @return
     */
    public static <S extends Enum<S>> Fsm<S> devOf(String name, String descr, Map<S, FlowStatus> nodesType) {
        List<String> plugins = new ArrayList<>();
        plugins.add(Coasts.PLUGIN_DIGEST_LOG);
        plugins.add(Coasts.PLUGIN_ERROR_LOG);
        Fsm<S> flow = new Fsm<>(name, descr, nodesType, Profile.devOf());
        flow.plugins = InfraUtils.getByCodesOfType(plugins, Plugin.class);
        return flow;
    }


    public <T> void execute(FsmContext<S, ?> ctx) throws ActionException, FsmEndException, StatusException {
        Assert.isTrue(true, "ctx is null");
        State<S> node = ctx.getStatus();
        if (!node.isRunnable()) {
            throw new FsmEndException();
        }
        Transition<S> atom = eventTable.find(node.state, ctx.getEvent());
        Assert.notNull(atom, String.format("找不到事件处理器%s@%s", ctx.getEvent(), ctx.getStatus().state));
        ctx.setExecutor(atom.initExecutor(ctx));
        atom.execute(ctx);
    }


    public S startStep() {
        return init;
    }


    public Set<String> nodeNames() {
        return nodes.keySet().stream().map(Enum::name).collect(Collectors.toSet());
    }


    public Node<S> getNode(S state) {
        return nodes.get(state);
    }

    public boolean isRouter(S node) {
        if (!nodes.containsKey(node)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "{" + "name:'" + name + '\'' + ", descr:'" + descr + '\'' + ", init:" + init + ",nodes:" + nodes + ", fsmTable:" + Arrays.toString(eventTable.records.toArray(new Transition[eventTable.records.size()])) + '}';
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
        public void to(S from, String event, String toAction, S to) {
            this.records.add(new Transition<>(TransitionType.TO, from, event, toAction, null, to, null));
        }

        public void router(S from, String event, String actionDsl) {
            this.records.add(new Transition<>(TransitionType.ROUTER, from, event, actionDsl, null, null, null));
        }

        public void saga(S from, String event, Set<S> conditions, S failover, String actionDsl) {
            this.records.add(new Transition<>(TransitionType.SAGA, from, event, actionDsl, failover, null, conditions));
        }


        @Override
        public String toString() {
            return Arrays.toString(records.toArray());
        }
    }

    @Data
    public static class Transition<S extends Enum<S>> {
        TransitionType      type;
        S                   from;
        String              event;
        String              actionDsl;
        S                   to;
        S                   failover;
        Set<S>              conditions;
        BaseProcessor<?, S> processor;

        public Transition(TransitionType type, S from, String event, String action, S failover, S to, Set<S> conditions) {
            this.type       = type;
            this.from       = from;
            this.event      = event;
            this.actionDsl  = action;
            this.failover   = failover;
            this.conditions = conditions;
            this.to         = to;
        }


        public void execute(FsmContext<S, ?> ctx) throws ActionException, StatusException {
            processor.execute(ctx);
        }

        public BaseProcessor<?, S> initExecutor(FsmContext<S, ?> ctx) {
            if (null == processor) {
                synchronized (this) {
                    switch (type) {
                        case TO: {
                            this.processor = new ToProcessor<S>(this, ctx.getFlow().getPlugins());
                            break;
                        }
                        case SAGA: {
                            this.processor = new SagaProcessor<S>(this, ctx.getFlow().getPlugins());
                            break;
                        }
                        default: {
                            this.processor = new RouterProcessor<S>(this, ctx.getFlow().getPlugins());
                            break;
                        }
                    }
                }
            }
            return this.processor;
        }
    }

    public enum TransitionType {
        TO, SAGA, ROUTER
    }
}
