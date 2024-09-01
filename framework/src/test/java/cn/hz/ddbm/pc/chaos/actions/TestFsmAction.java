package cn.hz.ddbm.pc.chaos.actions;

import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.fsm.FsmCommandAction;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmRouterAction;
import org.springframework.stereotype.Component;

@Component
public class TestFsmAction implements FsmCommandAction, FsmRouterAction {
    @Override
    public void command(FsmContext ctx) throws ActionException {
        System.out.println("xxxx");
    }

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
