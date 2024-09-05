package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.newcore.Payload;

public interface SagaPayload<S extends Enum<S>> extends Payload<SagaState<S>> {


    S getSagaState();

    void setSagaState(S sagaState);

    SagaState.Offset getOffset();


    void setOffset(SagaState.Offset offset);

    Boolean getForward();

    void setForward(Boolean forward);

    @Override
    default SagaState<S> getState() {
        return new SagaState<>(this.getSagaState(), getOffset(), getForward());
    }

    @Override
    default void setState(SagaState<S> state) {
        setSagaState(state.getMaster());
        setOffset(state.getOffset());
        setForward(state.getIsForward());
    }

}
