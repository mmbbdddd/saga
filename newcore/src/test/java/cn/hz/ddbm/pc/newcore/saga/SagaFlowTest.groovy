package cn.hz.ddbm.pc.newcore.saga

import cn.hutool.core.lang.Pair
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaActionProxy
import spock.lang.Specification

import static org.mockito.ArgumentMatchers.any

class SagaFlowTest<TestEnum> extends Specification {
    SagaFlow sagaFlow;

    def setup() {
        sagaFlow = new SagaFlow("flow", [
                Pair.of(S.a, RemoteSagaActionProxy.class),
                Pair.of(S.b, RemoteSagaActionProxy.class),
                Pair.of(S.c, RemoteSagaActionProxy.class)
        ] as List);

    }

    def "is End where state=#state then expect: #expectedResult"() {
        given:


        expect:
        sagaFlow.isEnd(state) == expectedResult

        where:
        state                                                       || expectedResult
        new SagaState<?>(S.c, SagaState.Offset.su, Boolean.TRUE)    || true
        new SagaState<?>(S.a, SagaState.Offset.fail, Boolean.FALSE) || true
        new SagaState<?>(S.b, SagaState.Offset.fail, true)          || false
        new SagaState<?>(S.b, SagaState.Offset.task, false)         || false
    }

    def "get Worker where state=#state then expect: #expectedResult"() {
        given:

        expect:
        sagaFlow.getWorker(state).forward.task.master == expectedResult

        where:
        state || expectedResult
        S.a   || S.a
    }


    def "get Retry where state=#state then expect: #expectedResult"() {
        given:

        expect:
        sagaFlow.getRetry(state) == expectedResult

        where:
        state || expectedResult
        any() || 1
    }


    enum S {
        a, b, c
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme