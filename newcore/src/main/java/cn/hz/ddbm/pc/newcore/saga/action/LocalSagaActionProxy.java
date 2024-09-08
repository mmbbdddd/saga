package cn.hz.ddbm.pc.newcore.saga.action;

import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaFlow;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;


public class LocalSagaActionProxy<S extends Enum<S>> implements LocalSagaAction<S> {
    Class<? extends SagaAction> actionClass;
    LocalSagaAction<S>          action;

    public LocalSagaActionProxy(Class<? extends SagaAction> actionClass) {
        this.actionClass = actionClass;
    }

    public void localSaga(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws ActionException {
        SagaState<S> lastState = ctx.getState();
        try {
            ctx.getProcessor().plugin().pre(ctx);
            getOrInitAction().localSaga(ctx);
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

    @Override
    public void localSagaRollback(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws Exception {
        SagaState<S> lastState = ctx.getState();
        try {
            ctx.getProcessor().plugin().pre(ctx);
            getOrInitAction().localSagaRollback(ctx);
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


    @Override
    public String code() {
        return getOrInitAction().code();
    }

    private LocalSagaAction<S> getOrInitAction() {
        if (null == action) {
            synchronized (this) {
                this.action = (LocalSagaAction) ProcesorService.getAction(actionClass);
            }
        }
        return action;
    }


}
