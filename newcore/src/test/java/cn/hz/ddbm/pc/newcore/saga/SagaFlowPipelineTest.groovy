package cn.hz.ddbm.pc.newcore.saga

import cn.hutool.core.lang.Pair
import cn.hutool.json.JSONUtil
import com.google.common.collect.Lists
import org.junit.Before
import org.junit.Test

class SagaFlowPipelineTest {
    SagaModel sagaPipeline

    @Before
    void setUp() {
        sagaPipeline = new SagaModel("test", Lists.newArrayList(
                Pair.of(PayStateMachine.init, "payFreezedAction"),
                Pair.of(PayStateMachine.pay, "sendAction"),
                Pair.of(PayStateMachine.send, "payCommitAction")
        ));
    }

    @Test
    void testIsEnd() {
        println(JSONUtil.toJsonPrettyStr(sagaPipeline.pipeline))
    }

    @Test
    void testNextWorker() {

    }

    @Test
    void testNextState() {

    }

    @Test
    void testGetRetry() {

    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme