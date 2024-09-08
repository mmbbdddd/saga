package cn.hz.ddbm.pc.chaos.actions;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmAction;
import org.springframework.stereotype.Component;

@Component
public class TestFsmAction implements RemoteFsmAction {

    @Override
    public String code() {
        return "fsmAction";
    }


    @Override
    public void remoteFsm(FlowContext ctx) throws Exception {

    }

    @Override
    public Object remoteFsmQuery(FlowContext ctx) throws Exception {
        return null;
    }
}
