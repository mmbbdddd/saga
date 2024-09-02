package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.actions.fsm.FreezedAction;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.saga.actions.PayCommitAction;
import cn.hz.ddbm.pc.newcore.saga.actions.PayFreezedAction;
import cn.hz.ddbm.pc.newcore.saga.actions.SendAction;
import com.google.common.collect.Lists;

public class MockSagaPipeline {
    static SagaFlow<PayStateMachine> toSagaFlow() {

        SagaFlow flow = new SagaFlow<>("sagaTest", Lists.newArrayList(
                Pair.of(PayStateMachine.init, PayFreezedAction.class),
                Pair.of(PayStateMachine.pay, SendAction.class),
                Pair.of(PayStateMachine.send, PayCommitAction.class)
        ));

        flow.setProfile(Profile.devOf());

        return flow;
    }
}
