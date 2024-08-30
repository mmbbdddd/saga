package cn.hz.ddbm.pc.core;

import cn.hz.ddbm.pc.core.exception.IdempotentException;
import cn.hz.ddbm.pc.core.exception.StatusException;
import cn.hz.ddbm.pc.core.processor.saga.SagaProcessor;

public interface TransitionHandler  {
    void onEvent(SagaProcessor  processor, FsmContext  ctx) throws StatusException, IdempotentException;
}
