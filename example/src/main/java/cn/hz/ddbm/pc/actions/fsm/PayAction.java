package cn.hz.ddbm.pc.actions.fsm;

import cn.hz.ddbm.pc.fsm.PayState;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.fsm.FsmAction;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import org.springframework.stereotype.Component;

@Component
public class PayAction implements FsmAction<PayState> {
    @Override
    public String code() {
        return "payAction";
    }

    @Override
    public void execute(FsmContext<PayState> ctx) throws ActionException {

    }

    @Override
    public PayState executeQuery(FsmContext<PayState> ctx) throws NoSuchRecordException, ActionException {
        return PayState.su;
    }
}
