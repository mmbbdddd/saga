package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.FlowModel;

import java.util.Set;

public class FsmModel extends FlowModel<FsmState> {
    public FsmModel(String name, FsmState init, Set<FsmState> ends, Set<FsmState> tasks) {
        super(name, init, ends, tasks);
    }

}
