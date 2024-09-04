package cn.hz.ddbm.pc.actions.fsm;

import cn.hz.ddbm.pc.fsm.PayState;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.fsm.FsmAction;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class PayCommitAction implements FsmAction<PayState> {
    @Override
    public String code() {
        return "commitPayAction";
    }

    @Override
    public void execute(FsmContext<PayState> ctx) throws ActionException {

    }

    @Override
    public Object executeQuery(FsmContext<PayState> ctx) throws NoSuchRecordException, ActionException {
        return new HashMap<String, Object>() {{
            put("code", "0000");
        }};
    }
}
