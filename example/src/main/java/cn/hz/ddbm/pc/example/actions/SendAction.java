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

@Component
public class SendAction implements SagaAction<PayState> {

    public SendAction() {
    }

    @Override
    public String beanName() {
        return "sendAction";
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
//        return RandomUitl.random(Lists.newArrayList(PayState.freezed, PayState.sended, PayState.sended_failover));
        return RandomUitl.selectByWeight("f5", Sets.set(
                Pair.of(PayState.freezed,0.1),
                Pair.of(PayState.sended,0.8),
                Pair.of(PayState.sended_failover,0.1)
        ));
    }



}
