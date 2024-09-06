package cn.hz.ddbm.pc.newcore.chaos;


import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmRouter;

public interface ChaosHandler {
    Enum fsmRouter(FsmContext ctx, FsmRouter sFsmRouter);

}
