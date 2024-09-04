package cn.hz.ddbm.pc.newcore.test;

import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmAction;

public class NoneFsmAction<S extends Enum<S>> implements FsmAction<S>  {


    @Override
    public void execute(FsmContext<S> ctx) throws ActionException {

    }

    @Override
    public Object executeQuery(FsmContext<S> ctx) throws NoSuchRecordException, ActionException {
        return null;
    }


    @Override
    public String code() {
        return "none";
    }
}
