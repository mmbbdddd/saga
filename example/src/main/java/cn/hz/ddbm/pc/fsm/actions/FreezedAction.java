package cn.hz.ddbm.pc.fsm.actions;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.fsm.FsmWorker;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmAction;
import cn.hz.ddbm.pc.saga.PayState;
import org.springframework.stereotype.Component;

@Component
public class FreezedAction implements RemoteFsmAction<PayState> {

    @Override
    public String code() {
        return "freezedAction";
    }


    @Override
    public void remoteFsm(FlowContext<FsmFlow<PayState>, FsmState<PayState>, FsmWorker<PayState>> ctx) throws Exception {

    }

    @Override
    public Object remoteFsmQuery(FlowContext<FsmFlow<PayState>, FsmState<PayState>, FsmWorker<PayState>> ctx) throws Exception {
        return null;
    }
}
