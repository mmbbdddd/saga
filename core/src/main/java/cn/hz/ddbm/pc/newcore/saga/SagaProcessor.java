package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.newcore.FlowContext;

public interface SagaProcessor {
    void updateState(FlowContext<SagaState> ctx, Integer state, SagaWorker.Offset sagaState);
}
