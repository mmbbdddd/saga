package cn.hz.ddbm.pc.newcore.test;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.fsm.FsmWorker;
import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmAction;

public class NoneLocalFsmAction<S extends Enum<S>> implements LocalFsmAction<S> {




    @Override
    public String code() {
        return "none";
    }

    @Override
    public Object localFsm(FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) throws Exception {
        return null;
    }
}
