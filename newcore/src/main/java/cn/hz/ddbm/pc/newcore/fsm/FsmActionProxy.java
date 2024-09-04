package cn.hz.ddbm.pc.newcore.fsm;


import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;

public class FsmActionProxy<S extends Enum<S>> implements FsmAction<S> {
    Class<FsmAction> actionClass;
    FsmAction<S>     action;

    public FsmActionProxy(Class<FsmAction> actionClass) {
        this.actionClass = actionClass;
    }


    @Override
    public String code() {
        return getOrInitAction().code();
    }


    private FsmAction<S> getOrInitAction() {
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
