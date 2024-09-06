package cn.hz.ddbm.pc.actions.fsm;

import cn.hz.ddbm.pc.saga.PayState;
import cn.hz.ddbm.pc.saga.PayTest;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.action.FsmAction;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class FreezedAction implements FsmAction<PayState> {
    @Override
    public void execute(FsmContext<PayState> ctx) throws ActionException {
        PayTest.account.decrementAndGet();
        PayTest.freezed.incrementAndGet();
    }

    @Override
    public Object executeQuery(FsmContext<PayState> ctx) throws NoSuchRecordException, ActionException {

        return new HashMap<String, Object>() {{
            put("code", "0000");
        }};
    }

    @Override
    public String code() {
        return "freezedAction";
    }
}
