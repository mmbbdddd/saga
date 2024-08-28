package cn.hz.ddbm.pc.example.actions;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.SagaAction;
import cn.hz.ddbm.pc.core.utils.RandomUitl;
import cn.hz.ddbm.pc.example.PayState;
import cn.hz.ddbm.pc.example.PayTest;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FreezedRollbackAction  implements SagaAction<PayState> {

    public FreezedRollbackAction() {
    }

    @Override
    public String beanName() {
        return "freezedRollbackAction";
    }

    @Override
    public Boolean executeWhen(FsmContext<PayState, ?> ctx) {
        return Objects.equals(ctx.getState(),PayState.payed);
    }
    @Override
    public void execute(FsmContext<PayState, ?> ctx) throws Exception {
        PayTest.account.incrementAndGet();
    }


    @Override
    public PayState queryState(FsmContext<PayState, ?> ctx) throws Exception {
//        return RandomUitl.random(Lists.newArrayList(PayState.freezed_rollback, PayState.fail,PayState.manual, PayState.freezed_rollback_failover));
        return RandomUitl.selectByWeight("f2",Sets.set(
                Pair.of(PayState.freezed_rollback,0.1),
                Pair.of(PayState.fail,0.7),
                Pair.of(PayState.manual,0.1),
                Pair.of(PayState.freezed_rollback_failover,0.1)
        ));
    }



}
