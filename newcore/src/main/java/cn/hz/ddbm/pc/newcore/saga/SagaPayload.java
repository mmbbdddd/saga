package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.Payload;

public interface SagaPayload<S extends Enum<S>> extends Payload<SagaState<S>> {


    FlowStatus getStatus();

    void setStatus(FlowStatus status);

    S getSagaState();

    void setSagaState(S sagaState);

    SagaState.Offset getOffset();


    void setOffset(SagaState.Offset offset);

    SagaState.Direction getDirection();

    void setDirection(SagaState.Direction direction);

    @Override
    default SagaState<S> getState() {
        return new SagaState<>(this.getStatus(),this.getSagaState(), getOffset(), getDirection());
    }

    @Override
    default void setState(SagaState<S> state) {
        setStatus(state.getStatus());
        setSagaState(state.getMaster());
        setOffset(state.getOffset());
        setDirection(state.getDirection());
    }

}
