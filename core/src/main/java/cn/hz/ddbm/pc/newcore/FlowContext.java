package cn.hz.ddbm.pc.newcore;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.UUID;
import cn.hz.ddbm.pc.ProcesorService;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class FlowContext<F extends FlowModel<S>, S extends State, W extends Worker<?, ?>> {
    final     Serializable        id;
    final     F                   flow;
    final     Map<String, Object> session;
    final     Payload             payload;
    final     AtomicInteger       loopErrorTimes;
    transient S                   state;
    transient W                   worker;
    transient ProcesorService     processor;
    transient Action              action;
    transient Boolean             actionResult;
    transient String              uuid;

    public FlowContext(F flow, Payload<S> payload, Map<String, Object> session) {
        Assert.notNull(flow, "flow is null");
        Assert.notNull(payload, "payload is null");
        Assert.notNull(payload.getState(), "payload.state is null");
        this.id             = payload.getId();
        this.flow           = flow;
        this.payload        = payload;
        this.session        = session == null ? new HashMap<>() : session;
        this.state          = payload.getState();
        this.loopErrorTimes = new AtomicInteger(0);
        this.uuid           = UUID.fastUUID().toString(true);
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
