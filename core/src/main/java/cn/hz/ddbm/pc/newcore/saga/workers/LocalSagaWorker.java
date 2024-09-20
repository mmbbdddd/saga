package cn.hz.ddbm.pc.newcore.saga.workers;


import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.SagaFlow;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;
import cn.hz.ddbm.pc.newcore.saga.actions.LocalSagaActionProxy;

import static cn.hz.ddbm.pc.newcore.saga.SagaWorker.Offset.rollback;
import static cn.hz.ddbm.pc.newcore.saga.SagaWorker.Offset.task;

public class LocalSagaWorker extends SagaWorker {
    LocalSagaActionProxy action;

    public LocalSagaWorker( Integer index, Class<? extends SagaAction> actionType) {
        super(index);
        this.action = new LocalSagaActionProxy(actionType);
    }

    @Override
    public void execute(SagaContext ctx) {
        switch (ctx.state.offset) {
            case task:
                if (ctx.executeTimes() > ctx.getFlow().getRetry(ctx.getState())) {
                    ctx.state.setOffset(rollback);
                } else {
                    action.doSaga(ctx);
                    ctx.state.index++;
                    ctx.state.offset = task;
                }
                break;
            case rollback:
                try {
                    action.doSagaRollback(ctx);
                    ctx.state.index--;
                    ctx.state.offset = rollback;
                } catch (Exception e) {
                    //todo 当执行次数超限的时候，回滚
                    ctx.state.setFlowStatus(FlowStatus.MANUAL);
                }
                break;
        }

    }

    @Override
    public boolean isFail() {
        return false;
    }

    @Override
    public boolean isSu() {
        return false;
    }
}
