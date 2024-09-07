package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.OffsetState;
import lombok.Data;

import java.io.Serializable;

@Data
public class MockSagaPayload implements SagaPayload<PayStateMachine> {
    long                id;
    FlowStatus          status;
    PayStateMachine     sagaState;
    OffsetState         offset;
    SagaState.Direction direction;

    public MockSagaPayload() {
        this.id        = 1;
        this.status    = FlowStatus.RUNNABLE;
        this.sagaState = PayStateMachine.init;
        this.offset    = OffsetState.task;
        this.direction = SagaState.Direction.forward;
    }

    @Override
    public Serializable getId() {
        return id;
    }


}
