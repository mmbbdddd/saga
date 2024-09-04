package cn.hz.ddbm.pc.actions.fsm;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.fsm.PayState;
import cn.hz.ddbm.pc.fsm.PayTest;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmAction;
import cn.hz.ddbm.pc.newcore.utils.RandomUitl;
import org.assertj.core.util.Sets;
import org.springframework.stereotype.Component;

@Component
public class FreezedAction implements FsmAction<PayState> {
    @Override
    public void execute(FsmContext<PayState> ctx) throws ActionException {
        PayTest.account.decrementAndGet();
        PayTest.freezed.incrementAndGet();
    }

    @Override
    public PayState executeQuery(FsmContext<PayState> ctx) throws NoSuchRecordException, ActionException {

        return null;
    }

    @Override
    public String code() {
        return "freezedAction";
    }
}
