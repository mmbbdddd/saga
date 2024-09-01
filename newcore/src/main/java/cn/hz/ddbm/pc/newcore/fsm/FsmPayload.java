package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.Payload;
import lombok.Data;

import java.io.Serializable;

@Data
public class FsmPayload<S extends Enum<S>> implements Payload<FsmState<S>> {
    Serializable id;
    FlowStatus   status;
    S            fsmState;

    public FsmPayload(Serializable id, FlowStatus status, S fsmState) {
        this.id       = id;
        this.status   = status;
        this.fsmState = fsmState;
    }

    @Override
    public Serializable getId() {
        return id;
    }

    public FsmState<S> getState() {
        return new FsmState<>(fsmState);
    }

    public void setState(FsmState<S> state) {
        this.fsmState = state.code();
    }
}
