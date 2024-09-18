package cn.hz.ddbm.pc.idcardapply.actions;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmAction;

public class SendBizAction implements RemoteFsmAction {
    @Override
    public void remoteFsm(FlowContext ctx) throws Exception {

    }

    @Override
    public Object remoteFsmQuery(FlowContext ctx) throws Exception {
        return null;
    }

    @Override
    public String code() {
        return "sendBizAction";
    }
}
