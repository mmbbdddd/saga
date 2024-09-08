package cn.hz.ddbm.pc.newcore.fsm.action;


import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.fsm.FsmWorker;

/**
 * @param <S>
 */
public class LocalFsmActionProxy<S extends Enum<S>> implements LocalFsmAction<S> {
    Class<? extends LocalFsmAction> actionClass;
    LocalFsmAction<S>               action;

    public LocalFsmActionProxy(Class<? extends LocalFsmAction> actionClass) {
        this.actionClass = actionClass;
    }


    @Override
    public String code() {
        return getOrInitAction().code();
    }


    private LocalFsmAction<S> getOrInitAction() {
        if (null == action) {
            synchronized (this) {
                this.action = ProcesorService.getAction(actionClass);
            }
        }
        return action;
    }

    @Override
    public Object localFsm(FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) throws ActionException {
        FsmState lastState = ctx.getState();
        try {
            ctx.getProcessor().plugin().pre(ctx);
            Object result = getOrInitAction().localFsm(ctx);
            ctx.getProcessor().plugin().post(lastState, ctx);
            return result;
        } catch (Exception e) {
            ctx.getProcessor().plugin().error(lastState, e, ctx);
            throw new ActionException(e);
        } finally {
            ctx.getProcessor().plugin()._finally(ctx);
        }
    }


}
