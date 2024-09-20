package cn.hz.ddbm.pc.newcore.fsm.workers;


import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.config.ErrorCode;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmWorker;
import cn.hz.ddbm.pc.newcore.fsm.Router;
import cn.hz.ddbm.pc.newcore.fsm.actions.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.actions.LocalFsmActionProxy;

import static cn.hz.ddbm.pc.newcore.fsm.FsmWorker.Offset.failover;

public class FsmLocalWorker<S extends Enum<S>> extends FsmWorker<S> {
    LocalFsmActionProxy action;

    public FsmLocalWorker(FsmFlow<S> fsm, S from, Class<? extends LocalFsmAction> action, Router<S> router) {
        super(fsm, from, router);
        this.action = new LocalFsmActionProxy(action);
    }

    @Override
    public void execute(FsmContext<S> ctx) {
        ctx.setAction(action);
        //如果任务可执行
        Offset offset = ctx.state.offset;
        switch (offset) {
            case task:
                ctx.state.offset = failover;
                Object result = action.doLocal(ctx);
                S state = router.router(ctx, result);
                if (null == state) {
                    ctx.state.flowStatus   = FlowStatus.MANUAL;
                    ctx.errorMessage = ErrorCode.ROUTER_RESULT_EMPTY;
                } else if (state.equals(ctx.getState())) {
                    ctx.state.offset = failover;
                } else {
                    ctx.state.state  = state;
                    ctx.state.offset = Offset.task;
                }
                break;
            case failover:
                break;
        }
    }

}
