package cn.hz.ddbm.pc.example.actions;

import cn.hz.ddbm.pc.core.Action;
import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.utils.RandomUitl;
import cn.hz.ddbm.pc.example.PayTest;
import cn.hz.ddbm.pc.example.PayState;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Component;

@Component
public class PayQueryAction implements Action.QueryAction<PayState> {

    public PayQueryAction() {
    }

    @Override
    public String beanName() {
        return "payAction";
    }

    @Override
    public void execute(FsmContext<PayState, ?> ctx) throws Exception {

    }


    @Override
    public PayState query(FsmContext<PayState, ?> ctx) throws Exception {

        PayState queryState = RandomUitl.random(Lists.newArrayList(PayState.payed_failover, PayState.init, PayState.payed));
        switch (queryState) {
            case payed_failover: {
                break;
            }
            case init: {
                PayTest.account.incrementAndGet();
                PayTest.freezed.decrementAndGet();
                break;
            }
        }

        return queryState;
    }
}
