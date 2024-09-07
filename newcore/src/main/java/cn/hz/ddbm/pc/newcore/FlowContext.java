package cn.hz.ddbm.pc.newcore;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.support.ActionResult;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class FlowContext<F extends FlowModel<S>, S extends State, W extends Worker<?>> {
    final     Serializable        id;
    final     F                   flow;
    final     Map<String, Object> session;
    final     Payload<S, F>       payload;
    final     AtomicInteger       loopErrorTimes;
    transient S                   state;
    transient W                   worker;
    transient ProcesorService     processor;
    transient Action              action;
    transient ActionResult        actionResult;

    public FlowContext(F flow, Payload<S,F> payload, Map<String, Object> session) {
        Assert.notNull(flow, "flow is null");
        Assert.notNull(payload, "payload is null");
        this.id             = payload.getId();
        this.flow           = flow;
        this.payload        = payload;
        this.session        = session == null ? new HashMap<>() : session;
        this.state          = payload.getState();
        this.loopErrorTimes = new AtomicInteger(0);
    }


    public void setState(S nextState) {
        this.state = nextState;
    }

    public void syncpayload() {
        payload.setState(state);
    }

    public Profile getProfile() {
        return flow.getProfile();
    }
}
