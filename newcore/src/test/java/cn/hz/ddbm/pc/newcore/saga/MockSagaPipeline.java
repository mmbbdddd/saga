package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.saga.actions.PayCommitAction;
import cn.hz.ddbm.pc.newcore.saga.actions.PayFreezedAction;
import cn.hz.ddbm.pc.newcore.saga.actions.SendAction;

import java.util.ArrayList;

public class MockSagaPipeline {
    static SagaFlow<PayStateMachine> toSagaFlow() {

        SagaFlow flow = new SagaFlow<>("sagaTest", new ArrayList<>());

//        new Tables<>(
//                new ArrayList<Triple<PayStateMachine, PayStateMachine, Class<? extends Action>>>() {{
//                    add(Triple.of(PayStateMachine.init, PayStateMachine.fail, PayFreezedAction.class));
//                    add(Triple.of(PayStateMachine.freezed, PayStateMachine.freeze_rollback, SendAction.class));
//                    add(Triple.of(PayStateMachine.sended, PayStateMachine.send_rollback, PayCommitAction.class));
//                    add(Triple.of(PayStateMachine.su, null, PayCommitAction.class));
//                }}
//
//        )
        flow.setProfile(Profile.chaosOf());

        return flow;
    }
}
