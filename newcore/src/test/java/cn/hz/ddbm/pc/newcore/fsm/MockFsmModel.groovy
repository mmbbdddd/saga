package cn.hz.ddbm.pc.newcore.fsm

class MockFsmModel {
    static FsmModel toFsmFlow() {
        return new FsmModel(
                "fsm", "pay"
                , ["su", "fail", "error"] as Set
                , ["send"] as Set
        )
    }
}
