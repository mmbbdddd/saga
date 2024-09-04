package cn.hz.ddbm.pc.actions.fsm;

import cn.hz.ddbm.pc.fsm.PayState;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.fsm.FsmAction;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;

public class PayRollbackAction implements FsmAction<PayState> {
    @Override
    public void execute(FsmContext<PayState> ctx) throws ActionException {

    }

    @Override
    public PayState executeQuery(FsmContext<PayState> ctx) throws NoSuchRecordException, ActionException {
        return null;
    }

    @Override
    public String code() {
        return null;
    }
}
