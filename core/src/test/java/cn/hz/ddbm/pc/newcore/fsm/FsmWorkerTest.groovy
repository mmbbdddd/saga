package cn.hz.ddbm.pc.newcore.fsm

import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmActionProxy

import cn.hz.ddbm.pc.newcore.fsm.worker.FsmRemoteWorker
import spock.lang.Specification
import spock.lang.Unroll

class FsmWorkerTest extends Specification {


    @Unroll
    def "of where router=#router and action=#action and from=#from then expect: #expectedResult"() {
        expect:
        ((FsmRemoteWorker) FsmWorker.remote(from, action, router)).from.state == expectedResult

        where:
        router                                                                                      | action                     | from || expectedResult
        new RemoteRouter<S>("noRecordExpression", "prcessingExpression", ["stateExpressions": S.a]) | RemoteFsmActionProxy.class | S.a  || S.a
    }

    enum S {
        a, b, c, d
    }

}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme