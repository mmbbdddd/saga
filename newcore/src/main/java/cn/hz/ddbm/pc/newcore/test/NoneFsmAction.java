package cn.hz.ddbm.pc.newcore.test;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.fsm.FsmWorker;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmAction;

public class NoneFsmAction<S extends Enum<S>> implements RemoteFsmAction<S> {


    @Override
    public void remoteFsm(FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) throws ActionException {

    }

    @Override
    public Object remoteFsmQuery(FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) throws NoSuchRecordException, ActionException {
        return null;
    }


    @Override
    public String code() {
        return "none";
    }
}
