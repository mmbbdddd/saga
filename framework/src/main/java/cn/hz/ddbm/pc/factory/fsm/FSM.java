package cn.hz.ddbm.pc.factory.fsm;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.fsm.FsmAction;
import lombok.Data;

import java.util.*;
import java.util.function.Function;
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
     * 流程变迁设置，包含三种类型
     * 事务业务：saga
     * 非事务业务：to
     * 查询业务：router
     *
     * @param
     */
    void transitions(Transitions<S> transitions);

    /**
     * 流程的配置，例如状态管理，会话管理，缺省重试次数，超时事件，节点属性，atcion属性等
     *
     * @return
     */
    Profile profile();


    Class<S> type();


    default FsmFlow<S> build() throws Exception {
        List<Pair<S, FlowStatus.Type>> nodes = nodes(new ArrayList<>());

        S       init  = getInitNode(nodes);
        Set<S>  ends  = getEndNodes(nodes);
        Set<S>  tasks = getTaskNodes(nodes);
        FsmFlow flow  = new FsmFlow(fsmId(), init, ends, tasks);

        transitions(new Transitions<>(flow));
//        flow.saga()

        Profile profile = profile();
        flow.profile(profile);

        profile.setPlugins(plugins(new ArrayList<>()));
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


    class Transitions<S extends Enum<S>> {
        FsmFlow<S> flow;
        State<S>   state;

        public Transitions(FsmFlow<S> flow) {
            this.flow = flow;
        }

        public State<S> state(S payState) {
            this.state = new State<>(payState, this);
            return state;
        }
    }

    class State<S extends Enum<S>> {
        Transitions<S> transitions;
        S              from;

        public State(S from, Transitions<S> transitions) {
            this.from        = from;
            this.transitions = transitions;
        }

        public State<S> onEventRouter(String event, Class<? extends FsmAction> action, Map<S,String> router) {
            transitions.flow.router(from, event, action);
            return this;
        }

        public Transitions<S> endState() {
            return transitions;
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