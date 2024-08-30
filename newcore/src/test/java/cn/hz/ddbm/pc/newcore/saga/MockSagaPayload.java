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


//
//    @Override
//    public Tetrad<FlowStatus, PayStateMachine, SagaState.Offset, Boolean> getSagaStatus() {
//        return Tetrad.of(status, state, offset, forward);
//    }
//
//    @Override
//    public void setSagaStatus(Tetrad<FlowStatus, PayStateMachine, SagaState.Offset, Boolean> t) {
//        this.status  = t.getOne();
//        this.state   = t.getTwo();
//        this.offset  = t.getThree();
//        this.forward = t.getFour();
//    }
}
