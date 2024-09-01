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
    Boolean          forward;

    public MockSagaPayload() {
        this.id        = 1;
        this.status    = FlowStatus.RUNNABLE;
        this.sagaState = PayStateMachine.init;
        this.offset    = SagaState.Offset.task;
        this.forward   = true;
    }

    @Override
    public Serializable getId() {
        return id;
    }


}
