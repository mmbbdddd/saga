package cn.hz.ddbm.pc.newcore.fsm.workers;


import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.config.ErrorCode;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.fsm.FsmWorker;
import cn.hz.ddbm.pc.newcore.fsm.Router;
import cn.hz.ddbm.pc.newcore.fsm.actions.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.actions.LocalFsmActionProxy;

public class FsmLocalWorker  extends FsmWorker  {
    LocalFsmActionProxy  action;

    public FsmLocalWorker(FsmFlow  fsm, Enum from, Class<? extends LocalFsmAction> action, Router  router) {
        super(fsm, from, router);
        this.action = new LocalFsmActionProxy (action);
    }

    @Override
    public void execute(FlowContext<FsmState > ctx) throws Exception {
        ctx.setAction(action);
        //如果任务可执行
        Offset  offset       = ctx.state.offset;
        switch (offset) {
            case task:
                Object result = action.doLocalFsm(ctx);
                Enum state = router.router(ctx, result);
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
