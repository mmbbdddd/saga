package cn.hz.ddbm.pc.newcore;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.newcore.support.ActionResult;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public abstract class FlowContext<F extends FlowModel<S>, S extends State, W extends Worker<?>> {
    final     Serializable        id;
    final     F                   flow;
    final     Map<String, Object> session;
    final     Payload<S>          payload;
    final     Profile             profile;
    transient FlowStatus          status;
    transient S                   state;
    transient W                   worker;
    transient FlowProcessor<?>    processor;
    transient Action              action;
    transient ActionResult        actionResult;
    //todo
    AtomicInteger executeTimes;

    public FlowContext(F flow,  Payload<S> payload, Profile profile, Map<String, Object> session) {
        Assert.notNull(flow, "flow is null");
        Assert.notNull(payload, "payload is null");
        Assert.notNull(profile, "profile is null");
        this.id           = payload.getId();
        this.flow         = flow;
        this.payload      = payload;
        this.profile      = profile;
        this.session      = session == null ? new HashMap<>() : session;
        this.state        = payload.getState();
        this.status       = payload.getStatus();
        this.executeTimes = new AtomicInteger(0);
    }

    public void metricsNode(FlowContext ctx) {
        executeTimes.incrementAndGet();
    }

    public abstract Integer getRetry(S state);

    public FlowProcessor<?> getProcessor() {
        return processor;
    }

    public void setState(S state) {
        if (getFlow().getEnds().contains(state)) {
            this.setStatus(FlowStatus.FINISH);
        }
        this.state = state;
    }

    public void syncpayload() {
        payload.setStatus(status);
        payload.setState(state);
    }
}
