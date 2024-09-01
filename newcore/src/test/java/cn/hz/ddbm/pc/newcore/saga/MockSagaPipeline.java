package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.Profile;
import com.google.common.collect.Lists;

public class MockSagaPipeline {
    static SagaFlow<PayStateMachine> toSagaFlow() {

        SagaFlow flow =  new SagaFlow<>("sagaTest", Lists.newArrayList(
                Pair.of(PayStateMachine.init, "payFreezedAction"),
                Pair.of(PayStateMachine.pay, "sendAction"),
                Pair.of(PayStateMachine.send, "payCommitAction")
        ));

        flow.setProfile(Profile.devOf());

        return flow;
    }
}
