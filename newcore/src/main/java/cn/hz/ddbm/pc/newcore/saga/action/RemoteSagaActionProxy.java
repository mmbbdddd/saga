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
        SagaState lastState = ctx.getState();
        try {
            ctx.getProcessor().plugin().pre(ctx);
            getOrInitAction().remoteSaga(ctx);
            ctx.setActionResult(true);
            ctx.getProcessor().plugin().post(lastState, ctx);
        } catch (Exception e) {
            ctx.setActionResult(false);
            ctx.getProcessor().plugin().error(lastState, e, ctx);
            throw new ActionException(e);
        } finally {
            ctx.getProcessor().plugin()._finally(ctx);
        }
    }


    public Boolean remoteSagaQuery(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws NoSuchRecordException, ActionException {
        SagaState lastState = ctx.getState();
        try {
            ctx.getProcessor().plugin().pre(ctx);
            Boolean result = getOrInitAction().remoteSagaQuery(ctx);
            ctx.setActionResult(true);
            ctx.getProcessor().plugin().post(lastState, ctx);
            return result;
        } catch (Exception e) {
            ctx.setActionResult(false);
            ctx.getProcessor().plugin().error(lastState, e, ctx);
            throw new ActionException(e);
        } finally {
            ctx.getProcessor().plugin()._finally(ctx);
        }
    }


    public void remoteSagaRollback(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws ActionException {
        SagaState lastState = ctx.getState();
        try {
            ctx.getProcessor().plugin().pre(ctx);
            getOrInitAction().remoteSagaRollback(ctx);
            ctx.setActionResult(true);
            ctx.getProcessor().plugin().post(lastState, ctx);
        } catch (Exception e) {
            ctx.setActionResult(false);
            ctx.getProcessor().plugin().error(lastState, e, ctx);
            throw new ActionException(e);
        } finally {
            ctx.getProcessor().plugin()._finally(ctx);
        }
    }

    public Boolean remoteSagaRollbackFailover(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws NoSuchRecordException, ActionException {
        SagaState lastState = ctx.getState();
        try {
            ctx.getProcessor().plugin().pre(ctx);
            Boolean result = getOrInitAction().remoteSagaRollbackFailover(ctx);
            ctx.setActionResult(true);
            ctx.getProcessor().plugin().post(lastState, ctx);
            return result;
        } catch (Exception e) {
            ctx.setActionResult(false);
            ctx.getProcessor().plugin().error(lastState, e, ctx);
            throw new ActionException(e);
        } finally {
            ctx.getProcessor().plugin()._finally(ctx);
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
