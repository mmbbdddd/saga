package cn.hz.ddbm.pc.newcore.saga.action;

import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.support.ActionResult;


public class LocalSagaActionProxy implements LocalSagaAction {
    Class<? extends LocalSagaAction> actionClass;
    LocalSagaAction                       action;

    public LocalSagaActionProxy(Class<? extends LocalSagaAction> actionClass) {
        this.actionClass = actionClass;
    }

    public void execute(SagaContext ctx) throws ActionException {
        try {
            getOrInitAction().execute(ctx);
            ctx.setActionResult(ActionResult.success());
        } catch (Exception e) {
            ctx.setActionResult(ActionResult.fail(e.getMessage()));
            throw new ActionException(e);
        }
    }

    @Override
    public void rollback(SagaContext ctx) {

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
