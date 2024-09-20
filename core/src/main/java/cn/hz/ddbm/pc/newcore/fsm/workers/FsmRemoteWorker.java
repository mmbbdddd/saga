package cn.hz.ddbm.pc.newcore.fsm.workers;


import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.config.ErrorCode;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.fsm.FsmWorker;
import cn.hz.ddbm.pc.newcore.fsm.Router;
import cn.hz.ddbm.pc.newcore.fsm.actions.RemoteFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.actions.RemoteFsmActionProxy;

import static cn.hz.ddbm.pc.newcore.fsm.FsmWorker.Offset.failover;

public class FsmRemoteWorker  extends FsmWorker  {
    RemoteFsmActionProxy  action;

    public FsmRemoteWorker(FsmFlow  fsm, Enum from, Class<? extends RemoteFsmAction> action, Router  router) {
        super(fsm, from, router);
        this.action = new RemoteFsmActionProxy(action);
    }

    @Override
    public void execute(FlowContext<FsmState > ctx) {
        ctx.setAction(action);
        //如果任务可执行
        Offset offset = ctx.state.offset;
        switch (offset) {
            case task:
                ctx.state.offset = failover;
                action.doRemoteFsm(ctx);
                break;
            case failover:
                Object result = action.remoteFsmQuery(ctx);
                Enum state = router.router(ctx, result);
                if (null == state) {
                    ctx.state.flowStatus = FlowStatus.MANUAL;
                    ctx.errorMessage     = ErrorCode.ROUTER_RESULT_EMPTY;
                } else {
                    ctx.state.state  = state;
                    ctx.state.offset = Offset.task;
                }
                execute(ctx);
                break;
        }
    }

}
