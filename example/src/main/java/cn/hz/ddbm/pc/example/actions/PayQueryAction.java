package cn.hz.ddbm.pc.example.actions;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.QueryAction;
import cn.hz.ddbm.pc.core.utils.RandomUitl;
import cn.hz.ddbm.pc.example.PayState;
import cn.hz.ddbm.pc.example.PayTest;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Component;

@Component
public class PayQueryAction implements QueryAction<PayState> {

    public PayQueryAction() {
    }

    @Override
    public String beanName() {
        return "payAction";
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
