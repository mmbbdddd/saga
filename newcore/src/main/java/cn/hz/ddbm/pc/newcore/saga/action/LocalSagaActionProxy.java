package cn.hz.ddbm.pc.newcore.saga.action;

import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.support.ActionResult;


public class LocalSagaActionProxy implements LocalSagaAction {
    Class<? extends LocalSagaAction> actionClass;
    LocalSagaAction                  action;

    public LocalSagaActionProxy(Class<? extends LocalSagaAction> actionClass) {
        this.actionClass = actionClass;
    }

    public void execute(SagaContext ctx) throws ActionException {
        SagaState lastState = (SagaState) ctx.getState();
        try {
            ctx.getProcessor().plugin().pre(ctx);
            getOrInitAction().execute(ctx);
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
    public void rollback(SagaContext ctx) throws Exception {
        SagaState lastState = (SagaState) ctx.getState();
        try {
            ctx.getProcessor().plugin().pre(ctx);
            getOrInitAction().rollback(ctx);
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

    private LocalSagaAction getOrInitAction() {
        if (null == action) {
            synchronized (this) {
                this.action = ProcesorService.getAction(actionClass);
            }
        }
        return action;
    }
}
