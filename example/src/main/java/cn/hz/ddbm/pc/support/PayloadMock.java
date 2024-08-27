package cn.hz.ddbm.pc.support;

import cn.hz.ddbm.pc.core.FsmPayload;
import cn.hz.ddbm.pc.core.enums.FlowStatus;

import java.io.Serializable;
import java.util.UUID;

public class PayloadMock<S extends Enum<S>> implements FsmPayload<S> {
    String     id;
    FlowStatus status;
    S          state;

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
    public FlowStatus getStatus() {
        return status;
    }

    @Override
    public S getState() {
        return state;
    }

    @Override
    public void setStatusSate(FlowStatus status, S state) {
        this.status = status;
        this.state  = state;
    }


}
