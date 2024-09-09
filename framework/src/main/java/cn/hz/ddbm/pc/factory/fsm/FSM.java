package cn.hz.ddbm.pc.factory.fsm;


import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.fsm.Router;
import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmAction;
import lombok.Data;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public interface FSM<S extends Enum<S>> {
    /**
     * 状态机ID
     *
     * @return
     */
    String flowId();

    /**
     * 状态机说明
     *
     * @return
     */
    String describe();

    /**
     * @return
     */
    S init();

    /**
     * @return
     */
    Set<S> ends();

    /**
     * 定义插件，在每个节点执行前后执行。
     * 常用的插件有日志插件，监控埋点插件……
     *
     * @return
     */
    List<Plugin> plugins();

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
        S          init  = init();
        Set<S>     ends  = ends();
        Set<S>     tasks = getTaskNodes();
        FsmFlow<S> flow  = new FsmFlow<>(flowId(), init, ends, tasks);

        transitions(new Transitions<>(flow));
//        flow.saga()

        Profile profile = profile();
        flow.profile(profile);

        profile.setPlugins(plugins());
        return flow;
    }


    default Set<S> getTaskNodes() {
        Set<S> all = EnumSet.allOf(type());
        all.removeAll(ends());
        all.remove(init());
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

        public State<S> local(String event, Class<? extends LocalFsmAction> action, Router<S> router) {
            transitions.flow.local(from, event, action, router);
            return this;
        }

        public State<S> remote(String event, Class<? extends RemoteFsmAction> action, Router<S> router) {
            transitions.flow.remote(from, event, action, router);
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