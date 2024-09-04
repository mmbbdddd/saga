package cn.hz.ddbm.pc.newcore.fsm


import cn.hz.ddbm.pc.newcore.FlowStatus
import cn.hz.ddbm.pc.newcore.Profile
import cn.hz.ddbm.pc.newcore.config.Coast
import com.google.common.collect.Sets
import org.junit.Test

import static cn.hz.ddbm.pc.newcore.fsm.FsmProcessorTest.PayFsm.*

class FsmProcessorTest {
    FsmProcessor fsmProcessor = new FsmProcessor()

    @Test
    void testWorkerProcess() {

        PayFsmPayload payload = new PayFsmPayload();
        FsmContext ctx = new FsmContext(
                new PayFsmFlow(), payload, new HashMap<String, Object>()
        )
        fsmProcessor.workerProcess(ctx)
    }

    @Test
    void testFlowProcess() {

        PayFsmPayload payload = new PayFsmPayload();
        FsmContext ctx = new FsmContext(
                new PayFsmFlow(), payload, new HashMap<String, Object>()
        )
        fsmProcessor.flowProcess(ctx)
    }

    class PayFsmPayload extends FsmPayload<PayFsm> {
        PayFsmPayload() {
            super(1, FlowStatus.RUNNABLE, init)
        }
    }

    class PayFsmFlow extends FsmFlow<PayFsm> {
        PayFsmFlow() {
            super("pay", PayFsm.init, Sets.newHashSet(su, fail, error), Sets.newHashSet(pay, send))
            this.to(PayFsm.init, Coast.FSM.EVENT_DEFAULT, "nono", pay);
            this.onEvent(pay, Coast.FSM.EVENT_DEFAULT, "nono", send);
            this.onEvent(send, Coast.FSM.EVENT_DEFAULT, "nono", send_failover);
            this.profile(Profile.devOf())
        }
    }

    enum PayFsm {
        init,
        pay,
        send,
        send_failover,
        su,
        fail,
        error,
    }


}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme