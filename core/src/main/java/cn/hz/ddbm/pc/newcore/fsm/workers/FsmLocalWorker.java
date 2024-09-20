package cn.hz.ddbm.pc.newcore.fsm.workers;


import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.config.ErrorCode;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.fsm.FsmWorker;
import cn.hz.ddbm.pc.newcore.fsm.Router;
import cn.hz.ddbm.pc.newcore.fsm.actions.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.actions.LocalFsmActionProxy;

public class FsmLocalWorker<S extends Enum<S>> extends FsmWorker<S> {
    LocalFsmActionProxy<S> action;

    public FsmLocalWorker(FsmFlow<S> fsm, S from, Class<? extends LocalFsmAction> action, Router<S> router) {
        super(fsm, from, router);
        this.action = new LocalFsmActionProxy<>(action);
    }

    @Override
    public void execute(FsmContext<S> ctx) throws ActionException {
        ctx.setAction(action);
        //如果任务可执行
        Offset  offset       = ctx.state.offset;
        switch (offset) {
            case task:
                Object result = action.doLocalFsm(ctx);
                S state = router.router(ctx, result);
                if (null == state) {
                    //如果结果为空，路由无结果，1，暂停，2报错
                    ctx.state.flowStatus = FlowStatus.MANUAL;
                    ctx.errorMessage     = ErrorCode.ROUTER_RESULT_EMPTY;
                } else if (state.equals(ctx.getState().state)) {
                    //如果状态不变，在告警（todo）告警，告警，告警，或者人工处理把。
                    ctx.state.flowStatus = FlowStatus.MANUAL;
                    ctx.errorMessage     = "节点执行后状态不变！！";
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
