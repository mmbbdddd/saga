package cn.hz.ddbm.pc.example.actions;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.SagaAction;
import cn.hz.ddbm.pc.core.utils.RandomUitl;
import cn.hz.ddbm.pc.example.PayState;
import cn.hz.ddbm.pc.example.PayTest;
import org.assertj.core.util.Sets;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BankMockAction implements SagaAction<PayState> {

    public BankMockAction() {
    }

    @Override
    public String beanName() {
        return "bankMockAction";
    }

    @Override
    public Boolean executeWhen(FsmContext<PayState, ?> ctx) {
        return Objects.equals(ctx.getState(),PayState.payed);
    }
    @Override
    public void execute(FsmContext<PayState, ?> ctx) throws Exception {
        PayTest.bank.incrementAndGet();
    }


    @Override
    public PayState queryState(FsmContext<PayState, ?> ctx) throws Exception {
//        return RandomUitl.random(Lists.newArrayList(PayState.payed, PayState.su, PayState.manual, PayState.payed_failover));
        return RandomUitl.selectByWeight("f4", Sets.set(
                Pair.of(PayState.payed,0.1),
                Pair.of(PayState.su,0.7),
                Pair.of(PayState.manual,0.1),
                Pair.of(PayState.payed_failover,0.1)
        ));

    }



}
