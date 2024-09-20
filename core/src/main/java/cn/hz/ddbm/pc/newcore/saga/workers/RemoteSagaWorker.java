package cn.hz.ddbm.pc.newcore.saga.workers;


import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;
import cn.hz.ddbm.pc.newcore.saga.actions.RemoteSagaActionProxy;

import static cn.hz.ddbm.pc.newcore.saga.SagaWorker.Offset.*;

public class RemoteSagaWorker extends SagaWorker {
    RemoteSagaActionProxy action;

    public RemoteSagaWorker(Integer index, Class<? extends SagaAction> actionType) {
        super(index);
        this.action = new RemoteSagaActionProxy(actionType);
    }

    @Override
    public void execute(FlowContext<SagaState> ctx) {
        switch (ctx.state.offset) {
            case task:
//                任务执行之前状态先设置为task_failover
                ctx.state.offset = task_failover;
                action.doRemoteSaga(ctx);
                break;
            case task_failover:
//                查询任务后递归执行状态机
                ctx.state.offset = (action.remoteSagaQuery(ctx));
                execute(ctx);
                break;
            case task_su:
//                成功则状态设置为下一个
                ctx.state.index++;
                ctx.state.offset = task;
                break;
            case task_fail:
//                失败择装填设置为就地回滚
                ctx.state.offset = rollback;
                break;
            case rollback:
//                任务执行之前状态先设置为rollback_failover
                ctx.state.offset = rollback_failover;
                action.doRemoteSagaRollback(ctx);
                break;
            case rollback_failover:
                ctx.state.offset = (action.remoteSagaRollbackQuery(ctx));
                execute(ctx);
                break;
            case rollback_su:
                ctx.state.index--;
                break;
            case rollback_fail:
                ctx.state.setFlowStatus(FlowStatus.MANUAL);
                break;
        }
    }

}
