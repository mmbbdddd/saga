package cn.hz.ddbm.pc.newcore.fsm;


import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;

import java.io.Serializable;

public class FsmActionProxy<S extends Serializable> implements FsmCommandAction<S>, FsmRouterAction<S> {
    Action action;

    public FsmActionProxy(Action action) {
        this.action = action;
    }


    @Override
    public String code() {
        return action.code();
    }

    @Override
    public void command(FsmContext<S> ctx) throws ActionException {
        try {
            ((FsmCommandAction)action).command(ctx);
        } catch (Exception e) {
            throw new ActionException(e);
        }
    }

    @Override
    public void execute(FsmContext<S> ctx) throws ActionException {
        try {
            ((FsmRouterAction<S>)action).execute(ctx);
        } catch (Exception e) {
            throw new ActionException(e);
        }
    }

    @Override
    public S executeQuery(FsmContext<S> ctx) throws NoSuchRecordException, ActionException {
        try {
            return  ((FsmRouterAction<S>)action).executeQuery(ctx);
        } catch (Exception e) {
            throw new ActionException(e);
        }
    }


}
