package cn.hz.ddbm.pc.actions.fsm;

import cn.hz.ddbm.pc.saga.PayState;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmAction;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class SendAction implements RemoteFsmAction<PayState> {
    @Override
    public void execute(FsmContext<PayState> ctx) throws ActionException {

    }

    @Override
    public Object executeQuery(FsmContext<PayState> ctx) throws NoSuchRecordException, ActionException {
        return new HashMap<String, Object>() {{
            put("code", "0000");
        }};
    }

    @Override
    public String code() {
        return "sendAction";
    }
}
