package cn.hz.ddbm.pc.newcore.fsm.action;


import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;

/**
 *
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
        try {
            getOrInitAction().execute(ctx);
        } catch (Exception e) {
            throw new ActionException(e);
        }
    }

    @Override
    public Object executeQuery(FsmContext<S> ctx) throws ActionException {
        try {
            return getOrInitAction().executeQuery(ctx);
        } catch (Exception e) {
            throw new ActionException(e);
        }
    }


}
