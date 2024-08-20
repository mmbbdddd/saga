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

@Getter
public class Fsm<S extends Enum<S>> {
    final String          name;
    final String          descr;
    final S               init;
    final Profile<S>      profile;
    final Map<S, Node<S>> nodes;
    @Setter
    List<Plugin> plugins;
    FsmTable<S> fsmTable;

    public Fsm(String name, String descr, Map<S, FlowStatus> nodesType, Profile<S> profile) {
        Assert.notNull(name, "flow.name is null");
        Assert.notNull(nodesType, "nodesType is null");
        Assert.notNull(profile, "profile is null");
        this.name     = name;
        this.descr    = descr;
        this.profile  = profile;
        this.nodes    = nodesType.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, t -> new Node<>(t.getKey(), t.getValue(), profile)));
        this.init     = this.nodes.values().stream().filter(t -> t.type.equals(FlowStatus.INIT)).findFirst().get().name;
        this.fsmTable = new FsmTable<>();
        this.plugins  = new ArrayList<>();
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


    public <T> void execute(FlowContext<S, ?> ctx) throws ActionException, FsmEndException, StatusException {
        Assert.isTrue(true, "ctx is null");
        State<S> node = ctx.getStatus();
        if (!node.isRunnable()) {
            throw new FsmEndException();
        }
        FsmRecord<S> atom = fsmTable.find(node.name, ctx.getEvent());
        Assert.notNull(atom, String.format("找不到事件处理器%s@%s", ctx.getEvent(), ctx.getStatus().name));
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
        return "{" + "name:'" + name + '\'' + ", descr:'" + descr + '\'' + ", init:" + init + ",nodes:" + nodes + ", fsmTable:" + Arrays.toString(fsmTable.records.toArray(new FsmRecord[fsmTable.records.size()])) + '}';
    }


    @Data
    public static class FsmTable<S extends Enum<S>> {
        private Set<FsmRecord<S>> records;

        public FsmTable() {
            this.records = new HashSet<>();
        }

        public FsmRecord<S> find(S node, String event) {
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
            this.records.add(new FsmRecord<>(FsmRecordType.TO, from, event, toAction, null, null, to, null));
        }

        public void router(S from, String event, String actionDsl, String router) {
            this.records.add(new FsmRecord<>(FsmRecordType.ROUTER, from, event, actionDsl, null, router, null, null));
        }

        public void saga(S from, String event, Set<S> conditions, S failover, String actionDsl, String router) {
            this.records.add(new FsmRecord<>(FsmRecordType.SAGA, from, event, actionDsl, failover, router, null, conditions));
        }


        @Override
        public String toString() {
            return Arrays.toString(records.toArray());
        }
    }

    @Data
    public static class FsmRecord<S extends Enum<S>> {
        FsmRecordType       type;
        S                   from;
        String              event;
        String              actionDsl;
        String              router;
        S                   to;
        S                   failover;
        Set<S>              conditions;
        BaseProcessor<?, S> action;

        public FsmRecord(FsmRecordType type, S from, String event, String action, S failover, String router, S to, Set<S> conditions) {
            this.type       = type;
            this.from       = from;
            this.event      = event;
            this.actionDsl  = action;
            this.failover   = failover;
            this.conditions = conditions;
            this.to         = to;
            this.router     = router;
        }


        public void execute(FlowContext<S, ?> ctx) throws ActionException, StatusException {
            action.execute(ctx);
        }

        public BaseProcessor<?, S> initExecutor(FlowContext<S, ?> ctx) {
            if (null == action) {
                synchronized (this) {
                    switch (type) {
                        case TO: {
                            this.action = new ToProcessor<S>(this, ctx.getFlow().getPlugins());
                            break;
                        }
                        case SAGA: {
                            this.action = new SagaProcessor<S>(this, ctx.getFlow().getPlugins());
                            break;
                        }
                        default: {
                            this.action = new RouterProcessor<S>(this, ctx.getFlow().getPlugins());
                            break;
                        }
                    }
                }
            }
            return this.action;
        }
    }

    public enum FsmRecordType {
        TO, SAGA, ROUTER
    }
}
