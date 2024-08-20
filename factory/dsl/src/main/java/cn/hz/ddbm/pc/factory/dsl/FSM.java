package cn.hz.ddbm.pc.factory.dsl;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import cn.hz.ddbm.pc.core.Fsm;
import cn.hz.ddbm.pc.core.Plugin;
import cn.hz.ddbm.pc.core.Profile;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.enums.FlowStatus;
import cn.hz.ddbm.pc.core.support.SessionManager;
import cn.hz.ddbm.pc.core.support.StatusManager;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FSM<S extends Enum<S>> {
    /**
     * 状态机ID
     * @return
     */
    String fsmId();

    /**
     * 状态机说明
     * @return
     */
    String describe();

    /**
     * 定义插件，在每个节点执行前后执行。
     * 常用的插件有日志插件，监控埋点插件……
     * @return
     */
    List<Plugin> plugins();

    /**
     * 参见profile
     * @return
     */
    SessionManager.Type session();

    /**
     *  参见profile
     * @return
     */
    StatusManager.Type status();

    /**
     * 定义流程编排的各节点
     * Map<节点，节点类型/>
     * @return
     */
    Map<S, FlowStatus> nodes();

    /**
     * 定义混沌模式下，每个节点可能的状态值。
     * Table<节点，事件,Set<Pair<目标节点,发生概率>>
     * @param table
     * @return
     */
    Table<S, String, Set<Pair<S, Double>>> maybeResults(Table<S, String, Set<Pair<S, Double>>> table);

    /**
     * 流程变迁设置，包含三种类型
     *  事务业务：saga
     *  非事务业务：to
     *  查询业务：router
     *
     * @param transitions
     */
    void transitions(Transitions<S> transitions);

    /**
     * 参见profile
     * @return
     */
    Map<S, Profile.StepAttrs> stateAttrs();
    /**
     * 参见profile
     */
    Map<String, Profile.ActionAttrs> actionAttrs();

    /**
     * 流程的配置，例如状态管理，会话管理，缺省重试次数，超时事件，节点属性，atcion属性等
     * @return
     */
    Profile<S> profile();

    /**
     * 节点>cron表达式
     * @return
     */
    Map<S,String> cron();


    default Fsm<S> build() throws Exception {
        Map<S, Profile.StepAttrs> stepAttrsMap = stateAttrs();
        Profile<S>                profile      = profile();
        profile.setStatusManager(status());
        profile.setSessionManager(session());
        profile.setActions(actionAttrs());
        profile.setStates(stepAttrsMap);
        Table<S, String, Set<Pair<S, Double>>> maybeResults = new RowKeyTable<>();
        profile.setMaybeResults(maybeResults(maybeResults));
        Fsm<S> fsm = Fsm.of(fsmId(), describe(), nodes(), profile);
        fsm.setPlugins(plugins());
        Transitions<S> transitions = new Transitions<>();
        transitions(transitions);
        transitions.transitions.forEach(t -> {
            if (t.getType().equals(Fsm.TransitionType.SAGA)) {
                fsm.getEventTable().saga(t.getFrom(), t.event, t.conditions, t.failover, t.action);
            }
            if (t.getType().equals(Fsm.TransitionType.ROUTER)) {
                fsm.getEventTable().router(t.getFrom(), t.getEvent(), t.getAction());
            }
            if (t.getType().equals(Fsm.TransitionType.TO)) {
                fsm.getEventTable().to(t.getFrom(), t.getEvent(), t.getAction(), t.to);
            }
        });
//        InfraUtils.getBean(PcService.class).addFlow(flow);
        return fsm;
    }


    class Transitions<S> {
        List<FSM.Transition<S>> transitions;

        public Transitions() {
            this.transitions = new ArrayList<>();
        }


        public Transitions<S> to(S from, String event, S to) {
            transitions.add(Transition.toOf(from, event, Coasts.NONE_ACTION, to));
            return this;
        }

        public Transitions<S> to(S from, String event, String action, S to) {
            transitions.add(Transition.toOf(from, event, action, to));
            return this;
        }

        public Transitions<S> router(S node, String event, String action) {
            transitions.add(Transition.routerOf(node, event, action));
            return this;
        }

        public Transitions<S> saga(S node, String event, Set<S> conditions, S failover, String action) {
            transitions.add(Transition.sagaOf(node, event, conditions, failover, action));
            return this;
        }
    }

    @Getter
    class Transition<S> {
        Fsm.TransitionType type;
        S                  from;
        String            event;
        Set<S>            conditions;
        S                 failover;
        String            action;
        S                 to;


        public static <S> Transition<S> routerOf(S node, String event, String action) {
            Transition<S> t = new Transition<S>();
            t.type   = Fsm.TransitionType.ROUTER;
            t.from   = node;
            t.event  = event;
            t.action = action;
            return t;
        }

        public static <S> Transition<S> toOf(S from, String event, String action, S to) {
            Transition<S> t = new Transition<S>();
            t.type   = Fsm.TransitionType.TO;
            t.from   = from;
            t.event  = event;
            t.action = action;
            t.to     = to;
            return t;
        }

        public static <S> Transition<S> sagaOf(S node, String event, Set<S> conditions, S failover, String action) {
            Transition<S> t = new Transition<S>();
            t.type       = Fsm.TransitionType.SAGA;
            t.from       = node;
            t.event      = event;
            t.action     = action;
            t.conditions = conditions;
            t.failover   = failover;
            return t;
        }
    }

}
