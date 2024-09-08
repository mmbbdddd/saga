package cn.hz.ddbm.pc.newcore.fsm.action;


import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;

/**
 * @param <S>
 */
public class RemoteFsmActionProxy<S extends Enum<S>> implements RemoteFsmAction<S> {
    Class<? extends RemoteFsmAction> actionClass;
    RemoteFsmAction<S>               action;

    public RemoteFsmActionProxy(Class<? extends RemoteFsmAction> actionClass) {
        this.actionClass = actionClass;
    }


    @Override
    public String code() {
        return getOrInitAction().code();
    }


    private RemoteFsmAction<S> getOrInitAction() {
        if (null == action) {
            synchronized (this) {
                this.action = ProcesorService.getAction(actionClass);
            }
        }
        return action;
    }

    @Override
    public void execute(FsmContext<S> ctx) throws ActionException {
        FsmState lastState = ctx.getState();
        try {
            ctx.getProcessor().plugin().pre(ctx);
            getOrInitAction().execute(ctx);
            ctx.getProcessor().plugin().post(lastState, ctx);
        } catch (Exception e) {
            ctx.getProcessor().plugin().error(lastState, e, ctx);
            throw new ActionException(e);
        } finally {
            ctx.getProcessor().plugin()._finally(ctx);
        }
    }

    @Override
    public Object executeQuery(FsmContext<S> ctx) throws ActionException {
        FsmState lastState = ctx.getState();
        try {
            ctx.getProcessor().plugin().pre(ctx);
            Object result = getOrInitAction().executeQuery(ctx);
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
