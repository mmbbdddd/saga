package cn.hz.ddbm.pc.newcore.fsm


import cn.hz.ddbm.pc.newcore.test.NoneRemoteFsmAction
import org.junit.Test

class FsmRouterActionTest<S> {
    @Test
    public void getgen() {
        NoneRemoteFsmAction a = new NoneRemoteFsmAction()
        println(a.getType())
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme