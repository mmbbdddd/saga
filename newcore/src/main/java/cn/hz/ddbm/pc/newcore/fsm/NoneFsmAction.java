package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;

import java.io.Serializable;

public class NoneFsmAction<S extends Serializable> implements FsmSagaAction<S>, FsmCommandAction<S> {
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
