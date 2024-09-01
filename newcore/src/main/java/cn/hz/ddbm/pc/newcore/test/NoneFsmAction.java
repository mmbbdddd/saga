package cn.hz.ddbm.pc.newcore.test;

import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.fsm.FsmCommandAction;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmRouterAction;

import java.io.Serializable;

public class NoneFsmAction<S extends Serializable> implements FsmRouterAction<S>, FsmCommandAction<S> {
    @Override
    public void command(FsmContext<S> ctx) throws ActionException {

    }

    @Override
    public void execute(FsmContext<S> ctx) throws ActionException {

    }

    @Override
    public S executeQuery(FsmContext<S> ctx) throws NoSuchRecordException, ActionException {
        return null;
    }



    @Override
    public String code() {
        return "none";
    }
}
