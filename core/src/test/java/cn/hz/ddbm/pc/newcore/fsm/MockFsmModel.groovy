package cn.hz.ddbm.pc.newcore.fsm

class MockFsmModel {
    static FsmFlow toFsmFlow() {
        return new FsmFlow(
                "fsm", "pay"
                , ["su", "fail", "error"] as Set
                , ["send"] as Set
        )
    }
}
