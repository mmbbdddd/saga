package cn.hz.ddbm.pc.chaos.actions;

import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmAction;
import org.springframework.stereotype.Component;

@Component
public class TestFsmAction implements RemoteFsmAction {

    @Override
    public void execute(FsmContext ctx) throws ActionException {
        System.out.println("zzzz");
    }

    @Override
    public Enum executeQuery(FsmContext ctx) throws NoSuchRecordException, ActionException {
        return null;
    }

    @Override
    public String code() {
        return "fsmAction";
    }
}
