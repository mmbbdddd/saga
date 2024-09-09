package cn.hz.ddbm.pc.newcore.saga.action;

import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaFlow;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;


public class RemoteSagaActionProxy<S extends Enum<S>> implements RemoteSagaAction<S> {
    Class<? extends SagaAction> actionClass;
    RemoteSagaAction                  action;

    public RemoteSagaActionProxy(Class<? extends SagaAction> actionClass) {
        this.actionClass = actionClass;
    }

    public void remoteSaga(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws ActionException {

        try {
            getOrInitAction().remoteSaga(ctx);
            ctx.setActionResult(true);
        } catch (Exception e) {
            ctx.setActionResult(false);
            throw new ActionException(e);
        }
    }


    public Boolean remoteSagaQuery(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws NoSuchRecordException, ActionException {
        try {
            Boolean result = getOrInitAction().remoteSagaQuery(ctx);
            ctx.setActionResult(true);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            ctx.setActionResult(false);
            throw new ActionException(e);
        }
    }


    public void remoteSagaRollback(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws ActionException {
        try {
            getOrInitAction().remoteSagaRollback(ctx);
            ctx.setActionResult(true);
        } catch (Exception e) {
            ctx.setActionResult(false);
            throw new ActionException(e);
        }
    }

    public Boolean remoteSagaRollbackFailover(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws NoSuchRecordException, ActionException {
        try {
            Boolean result = getOrInitAction().remoteSagaRollbackFailover(ctx);
            ctx.setActionResult(true);
            return result;
        } catch (Exception e) {
            ctx.setActionResult(false);
            throw new ActionException(e);
        }
    }

    @Override
    public String code() {
        return getOrInitAction().code();
    }

    private RemoteSagaAction getOrInitAction() {
        if (null == action) {
            synchronized (this) {
                this.action = (RemoteSagaAction) ProcesorService.getAction(actionClass);
            }
        }
        return action;
    }
}
