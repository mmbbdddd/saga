package cn.hz.ddbm.pc.example.actions;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.SagaAction;
import cn.hz.ddbm.pc.core.utils.RandomUitl;
import cn.hz.ddbm.pc.example.PayState;
import cn.hz.ddbm.pc.example.PayTest;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static cn.hz.ddbm.pc.example.PayState.freezed_rollback;

@Component
public class FreezedAction implements SagaAction<PayState> {

    public FreezedAction() {
    }

    @Override
    public String beanName() {
        return "freezedAction";
    }

    @Override
    public Boolean executeWhen(FsmContext<PayState, ?> ctx) throws Exception {
        return Objects.equals(ctx.getState(), PayState.init);
    }

    @Override
    public void execute(FsmContext<PayState, ?> ctx) throws Exception {
        PayTest.account.decrementAndGet();
        PayTest.freezed.incrementAndGet();
//        Logs.flow.info("{},{}支付扣款", ctx.getFlow().getName(), ctx.getId());
        ctx.getSession("");
    }


    @Override
    public PayState queryState(FsmContext<PayState, ?> ctx) throws Exception {
//        return RandomUitl.random(Lists.newArrayList(PayState.init, PayState.freezed, freezed_rollback, PayState.freezed_failover));
        return RandomUitl.selectByWeight("f1", Sets.set(
                Pair.of(PayState.init,0.1),
                Pair.of(PayState.freezed,0.7),
                Pair.of(freezed_rollback,0.1),
                Pair.of(PayState.freezed_failover,0.1)
        ));
    }

}
