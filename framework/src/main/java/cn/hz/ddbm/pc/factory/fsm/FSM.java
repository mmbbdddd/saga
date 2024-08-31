package cn.hz.ddbm.pc.factory.fsm;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.Profile.ProfileBuilder;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import lombok.Data;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public interface FSM<S extends Enum<S>> {
    /**
     * 状态机ID
     *
     * @return
     */
    String fsmId();

    /**
     * 状态机说明
     *
     * @return
     */
    String describe();

    /**
     * 定义插件，在每个节点执行前后执行。
     * 常用的插件有日志插件，监控埋点插件……
     *
     * @return
     */
    List<Plugin> plugins(List<Plugin> plugins);

    /**
     * 参见profile
     *
     * @return
     */
    Coast.SessionType session();

    /**
     * 参见profile
     *
     * @return
     */
    Coast.StatusType status();

    /**
     * 定义流程编排的各节点
     * Map<节点，节点类型/>
     *
     * @return
     */
    List<Pair<S, FlowStatus.Type>> nodes(List<Pair<S, FlowStatus.Type>> list);

    /**
     * 定义混沌模式下，每个节点可能的状态值。
     * Table<节点，事件,Set<Pair<目标节点,发生概率>>
     *
     * @return
     */
//    Table<S, SagaState.Offset, Double> errorProbability();

    /**
     * 流程变迁设置，包含三种类型
     * 事务业务：saga
     * 非事务业务：to
     * 查询业务：router
     *
     * @param
     */
    Transitions<S> transitions(Transitions<S> transitions);

    /**
     * 参见profile
     *
     * @return
     */
//    Map<S, Profile.StateAttrs> stateAttrs();

    /**
     * 参见profile
     */
//    Map<String, Profile.ActionAttrs> actionAttrs();

    /**
     * 流程的配置，例如状态管理，会话管理，缺省重试次数，超时事件，节点属性，atcion属性等
     *
     * @return
     */
    Profile profile(ProfileBuilder builder);


    Class<S> type();


    default FsmFlow<S> build() throws Exception {
        List<Pair<S, FlowStatus.Type>> nodes = nodes(new ArrayList<>());

        S       init  = getInitNode(nodes);
        Set<S>  ends  = getEndNodes(nodes);
        Set<S>  tasks = getTaskNodes(nodes);
        FsmFlow flow  = new FsmFlow(fsmId(), init, ends, tasks);

        transitions(new Transitions<S>()).getTransitions().forEach(t -> {
            if (t.getSaga()) {
                flow.saga(t.getFrom(), t.getEvent(), t.getAction(), t.getFailover());
            } else {
                flow.to(t.getFrom(), t.getEvent(), t.getAction(), t.to);
            }
        });
//        flow.saga()

        Profile profile = profile(Profile.builder());
        profile.setPlugins(plugins(new ArrayList<>()));
        flow.profile(profile);
        return flow;
    }

    default S getInitNode(List<Pair<S, FlowStatus.Type>> nodes) {
        return nodes.stream().filter(f -> f.getValue().equals(FlowStatus.Type.init)).findFirst().get().getKey();
    }

    default Set<S> getEndNodes(List<Pair<S, FlowStatus.Type>> nodes) {
        return nodes.stream()
                .filter(f -> f.getValue().equals(FlowStatus.Type.end))
                .map(Pair::getKey).collect(Collectors.toSet());
    }

    default Set<S> getTaskNodes(List<Pair<S, FlowStatus.Type>> nodes) {
        Set<S> all = EnumSet.allOf(type());
        all.removeAll(getEndNodes(nodes));
        all.remove(getInitNode(nodes));
        return all;
    }

    @Getter
    class Transitions<S> {
        List<Transition<S>> transitions;

        public Transitions() {
            this.transitions = new ArrayList<>();
        }

        public Transitions<S> saga(S from, String event, String action, S failover) {
            this.transitions.add(new Transition<>(
                    true, from, null, failover, action, event
            ));
            return this;
        }

        public Transitions<S> to(S from, String event, String action, S to) {
            this.transitions.add(new Transition<>(
                    false, from, to, null, action, event
            ));
            return this;
        }

    }

    @Data
    class Transition<S> {
        Boolean saga;
        S       from;
        S       to;
        S       failover;
        String  action;
        String  event;

        public Transition(Boolean saga, S from, S to, S failover, String action, String event) {
            this.saga     = saga;
            this.from     = from;
            this.to       = to;
            this.failover = failover;
            this.action   = action;
            this.event    = event;
        }
    }

}