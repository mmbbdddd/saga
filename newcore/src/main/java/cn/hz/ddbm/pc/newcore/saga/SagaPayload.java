package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.OffsetState;
import cn.hz.ddbm.pc.newcore.Payload;

public interface SagaPayload<S extends Enum<S>> extends Payload<SagaState<S>, SagaFlow<S>> {


    S getSagaState();

    void setSagaState(S sagaState);

    OffsetState getOffset();


    void setOffset(OffsetState offset);

    SagaState.Direction getDirection();

    void setDirection(SagaState.Direction direction);

    @Override
    default SagaState<S> getState() {
        return new SagaState<>(this.getSagaState(), getOffset(), getDirection());
    }

    @Override
    default void setState(SagaState<S> state) {
        setSagaState(state.getState());
        setOffset(state.getOffset());
        setDirection(state.getDirection());
    }

    @Override
    default FlowStatus getStatus(SagaFlow<S> flow) {
        return null;
    }
}
