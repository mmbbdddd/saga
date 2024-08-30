package cn.hz.ddbm.pc.core;


import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.core.action.SagaAction;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.enums.FlowStatus;
import cn.hz.ddbm.pc.core.processor.fsm.FsmProcessor;
import cn.hz.ddbm.pc.core.processor.saga.SagaProcessor;
import cn.hz.ddbm.pc.core.support.StatisticsSupport;
import cn.hz.ddbm.pc.core.utils.InfraUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Getter
public class FsmContext {
    //    入参
    private Serializable                      id;
    private Fsm                               flow;
    @Setter
    private Boolean                           fluent;
    @Setter
    private Profile                           profile;
    @Setter
    private Boolean                           mockBean = false;
    @Setter
    private Boolean                           isChaos  = false;
    @Setter
    private State                             nextNode;
    private Map<String, Object>               session;
    private Transition                        transition;
    private Processor                         processor;
    private Triple<FlowStatus, State, String> status;
    private FsmPayload                        data;


    public FsmContext(Fsm flow, FsmPayload data, String event, Profile profile) {
        Assert.notNull(flow, "flow is null");
        Assert.notNull(data, "data is null");
        Assert.notNull(event, "event is null");
        Assert.notNull(data.getId(), "date.id is null");
        Assert.notNull(data.getStatus(), "date.status is null");
        this.data    = data;
        this.id      = data.getId();
        this.flow    = flow;
        this.status  = data.getStatus();
        this.fluent  = true;
        this.session = new HashMap<>();
        //todo需要从应用中同步
        if (flow.getProfile() != null) {
            this.profile = flow.getProfile();
        } else {
            this.profile = profile;
//            flow.profile = profile;
        }
    }


    public void metricsNode(FsmContext ctx) {
        StatisticsSupport metricsWindows = InfraUtils.getMetricsTemplate();
        ///todo 待优化。这块有比较大的IO，可以合并到session中。
        metricsWindows.increment(ctx.getFlow().getName(), ctx.getId(), ctx.getState(), Coasts.EXECUTE_COUNT);
    }

    public State getState() {
        return status.getMiddle();
    }

    private <T> T getSessionOrDefault(String key, T deft) {
        return (T) session.computeIfAbsent(key, (Function<String, T>) s -> deft);
    }

    /**
     * 确保上下文状态一致
     * 1，status ==> entity
     */
    public void syncPayLoad() {
        data.setStatus(status);
    }


    public void getSession(String key) {
        session.get(key);
    }

    public void setSession(String key, Object value) {
        session.put(key, value);
    }

    public void initTransitionAndProcessor(Transition transition) {
        this.transition = transition;
        if (transition.getType().equals(ProcessorType.SAGA)) {
            this.processor = new SagaProcessor(this.transition, profile.getPlugins());
        } else {
            this.processor = new FsmProcessor(this.transition, profile.getPlugins());
        }
    }

    public String getEvent() {
        return status.getRight();
    }

    public void setEvent(String event) {
        this.status = Triple.of(status.getLeft(), status.getMiddle(), event);
    }

    public void setFlowStatus(FlowStatus status) {
        State  state = this.status.getMiddle();
        String event = this.status.getRight();
        this.status = Triple.of(status, state, event);
    }

    public FlowStatus getFlowStatus() {
        return this.status.getLeft();
    }

    public void setState(State newState) {
        FlowStatus flowStatus = this.status.getLeft();
        String     event      = this.status.getRight();
        this.status = Triple.of(flowStatus, newState, event);
    }

    public SagaAction getAction() {
        return null;
    }
}
