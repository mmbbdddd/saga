package cn.hz.ddbm.pc.example.actions;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.SagaAction;
import cn.hz.ddbm.pc.core.utils.RandomUitl;
import cn.hz.ddbm.pc.example.PayState;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Component;

@Component
public class SendAction implements SagaAction<PayState> {

    public SendAction() {
    }

    @Override
    public String beanName() {
        return "sendAction";
    }

    @Override
    public void execute(FsmContext<PayState, ?> ctx) throws Exception {

    }


    @Override
    public PayState query(FsmContext<PayState, ?> ctx) throws Exception {
        return RandomUitl.random(Lists.newArrayList(PayState.sended_failover, PayState.sended));
    }

}
