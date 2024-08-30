package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.support.ActionResult;
import cn.hz.ddbm.pc.newcore.test.NoneSagaAction;


public class SagaActionProxy implements SagaAction {
    String     action;
    SagaAction sagaAction;

    public SagaActionProxy(String action) {
        this.action = action;
    }

    public SagaActionProxy getOrInitAction() {
        if (null == sagaAction) {
            synchronized (this) {
//                this.sagaAction = getBean(action, SagaAction.class);
                this.sagaAction = new NoneSagaAction();
            }
        }
        return this;
    }


    public void execute(SagaContext<?> ctx) throws ActionException {
        try {
            sagaAction.execute(ctx);
            ctx.setActionResult(ActionResult.success());
        } catch (Exception e) {
            ctx.setActionResult(ActionResult.fail(e.getMessage()));
            throw new ActionException(e);
        }
    }


    public Boolean executeQuery(SagaContext<?> ctx) throws NoSuchRecordException, ActionException {
        try {
            ctx.setActionResult(ActionResult.success());
            return sagaAction.executeQuery(ctx);
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
            sagaAction.rollback(ctx);
            ctx.setActionResult(ActionResult.success());
        } catch (Exception e) {
            ctx.setActionResult(ActionResult.fail(e.getClass().getSimpleName()));
            throw new ActionException(e);
        }
    }

    public Boolean rollbackQuery(SagaContext<?> ctx) throws NoSuchRecordException, ActionException {
        try {
            ctx.setActionResult(ActionResult.success());
            return sagaAction.rollbackQuery(ctx);
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
        return action;
    }
}
