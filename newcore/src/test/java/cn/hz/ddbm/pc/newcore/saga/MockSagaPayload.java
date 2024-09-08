package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.newcore.FlowStatus;
import lombok.Data;

import java.io.Serializable;

@Data
public class MockSagaPayload implements SagaPayload<PayStateMachine> {
    long             id;
    FlowStatus       status;
    PayStateMachine  sagaState;
    SagaState.Offset offset;

    public MockSagaPayload() {
        this.id        = 1;
        this.status    = FlowStatus.RUNNABLE;
        this.sagaState = PayStateMachine.init;
        this.offset    = SagaState.Offset.task;
    }

    @Override
    public Serializable getId() {
        return id;
    }

    @Override
    public SagaState<PayStateMachine> getState() {
        return null;
    }

    @Override
    public void setState(SagaState<PayStateMachine> state) {

    }


}
