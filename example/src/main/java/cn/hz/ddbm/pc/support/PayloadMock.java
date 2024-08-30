package cn.hz.ddbm.pc.support;

import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.core.FsmPayload;
import cn.hz.ddbm.pc.core.State;
import cn.hz.ddbm.pc.core.enums.FlowStatus;

import java.io.Serializable;
import java.util.UUID;

public class PayloadMock<S extends State> implements FsmPayload<S> {
    String     id;
    FlowStatus status;
    S          state;
    String     event;

    public PayloadMock(S init) {
        this.id     = UUID.randomUUID().toString();
        this.state  = init;
        this.status = FlowStatus.INIT;
    }


    @Override
    public Serializable getId() {
        return id;
    }

    @Override
    public Triple<FlowStatus, S, String> getStatus() {
        return Triple.of(status, state, event);
    }

    @Override
    public void setStatus(Triple<FlowStatus, S, String> triple) {
        this.status = triple.getLeft();
        this.state  = triple.getMiddle();
        this.event  = triple.getRight();
    }
}
