package cn.hz.ddbm.pc.newcore.fsm;

import cn.hutool.core.map.multi.Table;
import cn.hz.ddbm.pc.newcore.FlowModel;

import java.util.Set;

public class FsmModel extends FlowModel<FsmState> {

    Table<FsmState, String, FsmWorker> transitionTable;

    public FsmModel(String name, FsmState init, Set<FsmState> ends, Set<FsmState> tasks) {
        super(name, init, ends, tasks);
    }

    public FsmWorker getWorker(FsmState state, String event) throws TransitionNotFoundException {
        FsmWorker worker = transitionTable.get(state, event);
        if (null == worker) {
            throw new TransitionNotFoundException();
        }
        return worker;
    }
}
