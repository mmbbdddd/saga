package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.support.ActionResult;


public class SagaActionProxy implements SagaAction {
    Class<? extends SagaAction> actionClass;
    SagaAction        action;

    public SagaActionProxy(Class<? extends SagaAction> actionClass) {
        this.actionClass = actionClass;
    }

    public void execute(SagaContext<?> ctx) throws ActionException {
        try {
            getOrInitAction().execute(ctx);
            ctx.setActionResult(ActionResult.success());
        } catch (Exception e) {
            ctx.setActionResult(ActionResult.fail(e.getMessage()));
            throw new ActionException(e);
        }
    }


    public Boolean executeQuery(SagaContext<?> ctx) throws NoSuchRecordException, ActionException {
        try {
            ctx.setActionResult(ActionResult.success());
            return getOrInitAction().executeQuery(ctx);
        } catch (NoSuchRecordException e) {
            ctx.setActionResult(ActionResult.fail(e.getClass().getSimpleName()));
            throw e;
        } catch (Exception e) {
            ctx.setActionResult(ActionResult.fail(e.getClass().getSimpleName()));
            throw new ActionException(e);
        }
    }


    public void rollback(SagaContext<?> ctx) throws ActionException {
        try {
            getOrInitAction().rollback(ctx);
            ctx.setActionResult(ActionResult.success());
        } catch (Exception e) {
            ctx.setActionResult(ActionResult.fail(e.getClass().getSimpleName()));
            throw new ActionException(e);
        }
    }

    public Boolean rollbackQuery(SagaContext<?> ctx) throws NoSuchRecordException, ActionException {
        try {
            ctx.setActionResult(ActionResult.success());
            return getOrInitAction().rollbackQuery(ctx);
        } catch (NoSuchRecordException e) {
            ctx.setActionResult(ActionResult.fail(e.getClass().getSimpleName()));
            throw e;
        } catch (Exception e) {
            ctx.setActionResult(ActionResult.fail(e.getClass().getSimpleName()));
            throw new ActionException(e);
        }
    }

    @Override
    public String code() {
        return getOrInitAction().code();
    }

    private SagaAction getOrInitAction() {
        if (null == action) {
            synchronized (this) {
                this.action = ProcesorService.getAction(actionClass);
            }
        }
        return action;
    }
}
