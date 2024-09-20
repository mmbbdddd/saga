package cn.hz.ddbm.pc.newcore.saga;

public interface SagaProcessor {
    void updateState(SagaContext ctx, Integer state, SagaWorker.Offset sagaState);
}
