package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.Payload;
import lombok.Data;

import java.io.Serializable;


public interface FsmPayload<S extends Enum<S>> extends Payload<FsmState<S>, FsmFlow<S>> {


    Serializable getId();

    void setId(Serializable id);

    S getFsmState();

    void setFsmState(S fsmState);

    FsmState.Offset getOffset();

    void setOffset(FsmState.Offset offset);

    default FsmState<S> getState() {
        return new FsmState<>(getFsmState(), getOffset());
    }

    default void setState(FsmState<S> state) {
        setFsmState(state.getState());
        setOffset(state.getOffset());
    }

    @Override
    default FlowStatus getStatus(FsmFlow<S> flow) {
        return null;
    }
}
