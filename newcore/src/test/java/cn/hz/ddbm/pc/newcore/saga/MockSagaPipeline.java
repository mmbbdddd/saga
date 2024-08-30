package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.core.lang.Pair;
import com.google.common.collect.Lists;

public class MockSagaPipeline {
    static SagaModel<PayStateMachine> toSagaFlow() {

        return new SagaModel<>("sagaTest", Lists.newArrayList(
                Pair.of(PayStateMachine.init, "payFreezedAction"),
                Pair.of(PayStateMachine.pay, "sendAction"),
                Pair.of(PayStateMachine.send, "payCommitAction")
        ));
    }
}
