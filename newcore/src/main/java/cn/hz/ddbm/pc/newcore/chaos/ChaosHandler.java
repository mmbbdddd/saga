package cn.hz.ddbm.pc.newcore.chaos;


import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmRouter;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;

public interface ChaosHandler {
    <S extends Enum<S>> S handleRouter(FsmContext<S> ctx, FsmRouter<S> sFsmRouter);

}
