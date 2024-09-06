package cn.hz.ddbm.pc.newcore.fsm.action;


import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;

/**
 *
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
    public Object execute(FsmContext<S> ctx) throws ActionException {
        try {
           return getOrInitAction().execute(ctx);
        } catch (Exception e) {
            throw new ActionException(e);
        }
    }


}
