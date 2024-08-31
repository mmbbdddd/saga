package cn.hz.ddbm.pc.factory.fsm;

import cn.hz.ddbm.pc.factory.Resource;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;

public class FsmResource extends Resource<FsmFlow> {
    FSM fsm;
    @Override
    public FsmFlow resolve() throws Exception {
        return fsm.build();
    }
}
