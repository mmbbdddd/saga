package cn.hz.ddbm.pc.example.actions;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.SagaAction;
import cn.hz.ddbm.pc.core.log.Logs;
import cn.hz.ddbm.pc.core.utils.RandomUitl;
import cn.hz.ddbm.pc.example.PayState;
import cn.hz.ddbm.pc.example.PayTest;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Component;

@Component
public class PayAction implements SagaAction<PayState> {

    public PayAction() {
    }

    @Override
    public String beanName() {
        return "payAction";
    }

    @Override
    public void execute(FsmContext<PayState, ?> ctx) throws Exception {
        PayTest.account.decrementAndGet();
        PayTest.freezed.incrementAndGet();
        Logs.flow.info("{},{}支付扣款", ctx.getFlow().getName(), ctx.getId());
        ctx.getSession("");
    }


    @Override
    public PayState query(FsmContext<PayState, ?> ctx) throws Exception {
        return RandomUitl.random(Lists.newArrayList(PayState.init, PayState.payed));
    }

}
