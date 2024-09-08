package cn.hz.ddbm.pc.newcore.saga.action;

import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.support.ActionResult;


public class RemoteSagaActionProxy implements RemoteSagaAction {
    Class<? extends RemoteSagaAction> actionClass;
    RemoteSagaAction                  action;

    public RemoteSagaActionProxy(Class<? extends RemoteSagaAction> actionClass) {
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


    public Boolean executeQuery(SagaContext ctx) throws NoSuchRecordException, ActionException {
        SagaState lastState = (SagaState) ctx.getState();
        try {
            ctx.getProcessor().plugin().pre(ctx);
            Boolean result =  getOrInitAction().executeQuery(ctx);
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


    public void rollback(SagaContext ctx) throws ActionException {
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

    public Boolean rollbackQuery(SagaContext ctx) throws NoSuchRecordException, ActionException {
        SagaState lastState = (SagaState) ctx.getState();
        try {
            ctx.getProcessor().plugin().pre(ctx);
            Boolean result =  getOrInitAction().rollbackQuery(ctx);
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
                this.action = ProcesorService.getAction(actionClass);
            }
        }
        return action;
    }
}
