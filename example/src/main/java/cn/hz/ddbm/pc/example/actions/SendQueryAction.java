package cn.hz.ddbm.pc.example.actions;

import cn.hz.ddbm.pc.core.Action;
import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.log.Logs;
import cn.hz.ddbm.pc.core.utils.RandomUitl;
import cn.hz.ddbm.pc.example.PayTest;
import cn.hz.ddbm.pc.example.PayState;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Component;

@Component
public class SendQueryAction implements Action.QueryAction<PayState> {
    @Override
    public String beanName() {
        return "sendQueryAction";
    }

    @Override
    public void execute(FsmContext<PayState, ?> ctx) throws Exception {

    }


    @Override
    public PayState query(FsmContext<PayState, ?> ctx) throws Exception {
        PayState queryState = RandomUitl.random(Lists.newArrayList(PayState.sended_failover, PayState.payed, PayState.sended, PayState.su, PayState.fail));
        switch (queryState) {
            case sended_failover: {
                break;
            }
            case su: {
                Logs.flow.info("{},{}支付成功", ctx.getFlow().getName(), ctx.getId());
                PayTest.freezed.decrementAndGet();
                PayTest.bank.incrementAndGet();
                break;
            }
            case fail: {
                Logs.flow.info("{},{}支付失败", ctx.getFlow().getName(), ctx.getId());
                PayTest.account.incrementAndGet();
                PayTest.freezed.decrementAndGet();
                break;
            }
        }
        return queryState;
    }
}
