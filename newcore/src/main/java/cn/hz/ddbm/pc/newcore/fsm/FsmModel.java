package cn.hz.ddbm.pc.newcore.fsm;

import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import cn.hz.ddbm.pc.newcore.FlowModel;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.TransitionNotFoundException;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

public class FsmModel<S extends Serializable> extends FlowModel<FsmState<S>> {

    Table<S, String, FsmWorker<S>> transitionTable;

    public FsmModel(String name, S init, Set<S> ends, Set<S> tasks) {
        super(name, new FsmState<S>(init),
                ends.stream().map(FsmState::new).collect(Collectors.toSet()),
                tasks.stream().map(FsmState::new).collect(Collectors.toSet()));
        this.transitionTable = new RowKeyTable<>();
    }

    public FsmWorker<S> getWorker(FsmState<S> state, String event) throws TransitionNotFoundException {
        FsmWorker<S> worker = transitionTable.get(state.getState(), event);
        if (null == worker) {
            throw new TransitionNotFoundException();
        }
        return worker;
    }

    public FsmModel<S> to(S from, String event, String action, S to) {
        ToFsmWorker<S> toFsmWorker = new ToFsmWorker<>(from, action, to);
        this.transitionTable.put(from, event, toFsmWorker);
        return this;
    }

    public FsmModel<S> saga(S from, String event, String action, S failover) {
        SagaFsmWorker<S> sagaFsmWorker = new SagaFsmWorker<>(from, action, failover);
        this.transitionTable.put(from, event, sagaFsmWorker);
        this.transitionTable.put(failover, Coast.FSM.EVENT_DEFAULT, sagaFsmWorker);
        return this;
    }
}
