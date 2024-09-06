package cn.hz.ddbm.pc.newcore.fsm

import cn.hz.ddbm.pc.newcore.fsm.router.RemoteRouter
import spock.lang.*

class FsmWorkerTest extends Specification {


    @Unroll
    def "of where router=#router and action=#action and from=#from then expect: #expectedResult"() {
        expect:
        ((SagaWorker)FsmWorker.of(from, action, router)).from.state == expectedResult

        where:
        router                                                                                   | action               | from || expectedResult
        new RemoteRouter<S>("noRecordExpression", "prcessingExpression", ["stateExpressions": S.a]) | FsmActionProxy.class | S.a || S.a
    }

    enum S {
        a, b, c, d
    }

}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme