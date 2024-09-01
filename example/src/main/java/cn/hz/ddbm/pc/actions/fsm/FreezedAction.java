package cn.hz.ddbm.pc.actions.fsm;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.fsm.PayState;
import cn.hz.ddbm.pc.fsm.PayTest;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmRouterAction;
import cn.hz.ddbm.pc.newcore.utils.RandomUitl;
import org.assertj.core.util.Sets;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Component
public class FreezedAction implements FsmRouterAction <PayState> {
    @Override
    public void execute(FsmContext<PayState> ctx) throws ActionException {
        PayTest.account.decrementAndGet();
        PayTest.freezed.incrementAndGet();
    }

    @Override
    public PayState executeQuery(FsmContext<PayState> ctx) throws NoSuchRecordException, ActionException {

        return RandomUitl.selectByWeight("f3", Sets.set(
                Pair.of(PayState.freezed,0.1),
                Pair.of(PayState.init,0.7)
        ));
    }

    @Override
    public String code() {
        return "freezedAction";
    }
}
