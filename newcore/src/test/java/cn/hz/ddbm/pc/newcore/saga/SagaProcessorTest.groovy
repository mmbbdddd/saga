package cn.hz.ddbm.pc.newcore.saga

import cn.hz.ddbm.pc.newcore.FlowContext
import org.junit.Test

class SagaProcessorTest {
    SagaProcessor sagaProcessor = new SagaProcessor()

    @Test
    void testWorkerProcess() {
        MockSagaPayload payload = new MockSagaPayload();
        FlowContext<SagaFlow, SagaState, SagaWorker> ctx = new FlowContext(
                MockSagaPipeline.toSagaFlow(), payload, new HashMap<String, Object>()
        )
        sagaProcessor.workerProcess(ctx)
    }

    @Test
    void testFlowProcessor() {
        MockSagaPayload payload = new MockSagaPayload();
        FlowContext<SagaFlow, SagaState, SagaWorker> ctx = new FlowContext(
                MockSagaPipeline.toSagaFlow(), payload, new HashMap<String, Object>()
        )
        sagaProcessor.flowProcess(ctx)
    }

}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme