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
public class SendAction implements FsmAction<PayState> {
    @Override
    public void execute(FsmContext<PayState> ctx) throws ActionException {

    }

    @Override
    public PayState executeQuery(FsmContext<PayState> ctx) throws NoSuchRecordException, ActionException {
        PayState result = RandomUitl.selectByWeight("f2", Sets.set(
                Pair.of(PayState.sended, 0.7)
        ));

        switch (result) {
            case sended: {
                return PayState.sended;
            }
            case su: {
                PayTest.freezed.decrementAndGet();
                PayTest.bank.incrementAndGet();
                return PayState.su;
            }
            case fail: {
                PayTest.account.incrementAndGet();
                PayTest.freezed.decrementAndGet();
                return PayState.fail;
            }
        }


        return result;
    }

    @Override
    public String code() {
        return "sendAction";
    }
}
