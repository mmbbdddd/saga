package cn.hz.ddbm.pc.example.actions;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.SagaAction;
import cn.hz.ddbm.pc.core.log.Logs;
import cn.hz.ddbm.pc.core.utils.RandomUitl;
import cn.hz.ddbm.pc.example.PayState;
import cn.hz.ddbm.pc.example.PayTest;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PayAction implements SagaAction<PayState> {

    public PayAction() {
    }

    @Override
    public String beanName() {
        return "payAction";
    }

    @Override
    public Boolean executeWhen(FsmContext<PayState, ?> ctx) throws Exception {
        return Objects.equals(ctx.getState(), PayState.init);
    }

    @Override
    public void execute(FsmContext<PayState, ?> ctx) throws Exception {
        PayTest.freezed.decrementAndGet();
//        Logs.flow.info("{},{}支付扣款", ctx.getFlow().getName(), ctx.getId());
        ctx.getSession("");
    }


    @Override
    public PayState queryState(FsmContext<PayState, ?> ctx) throws Exception {
//        return RandomUitl.random(Lists.newArrayList(PayState.sended, PayState.payed,PayState.freezed_rollback, PayState.payed_failover));
        return RandomUitl.selectByWeight("f3", Sets.set(
                Pair.of(PayState.sended,0.1),
                Pair.of(PayState.su,0.7),
                Pair.of(PayState.freezed_rollback,0.1),
                Pair.of(PayState.payed_failover,0.1)
        ));

    }

}
