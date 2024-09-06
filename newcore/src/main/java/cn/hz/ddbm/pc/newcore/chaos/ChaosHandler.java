package cn.hz.ddbm.pc.newcore.chaos;


import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmRouter;

public interface ChaosHandler {
    <S extends Enum<S>> S fsmRouter(FsmContext<S> ctx, FsmRouter<S> sFsmRouter);

}
