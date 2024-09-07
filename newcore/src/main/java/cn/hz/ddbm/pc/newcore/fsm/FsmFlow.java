package cn.hz.ddbm.pc.newcore.fsm;

import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import cn.hz.ddbm.pc.newcore.FlowModel;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.exception.TransitionNotFoundException;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.router.LocalRouter;
import cn.hz.ddbm.pc.newcore.fsm.router.LocalToRouter;
import cn.hz.ddbm.pc.newcore.fsm.router.RemoteRouter;

import java.util.Set;
import java.util.stream.Collectors;

public class FsmFlow<S extends Enum<S>> extends FlowModel<FsmState<S>> {

    Table<S, String, FsmWorker<S>> transitionTable;

    public FsmFlow(String name, S init, Set<S> ends, Set<S> tasks) {
        super(name, new FsmState<>(FlowStatus.INIT, init, FsmState.Offset.task), ends.stream()
                .map(e -> new FsmState<>(FlowStatus.FINISH, e, FsmState.Offset.task))
                .collect(Collectors.toSet()), tasks.stream()
                .map(e -> new FsmState<>(FlowStatus.RUNNABLE, e, FsmState.Offset.task))
                .collect(Collectors.toSet()));
        this.transitionTable = new RowKeyTable<>();
    }

    public FsmWorker<S> getWorker(FsmState<S> state, String event) throws TransitionNotFoundException {
        FsmWorker<S> worker = transitionTable.get(state.getState(), event);
        if (null == worker) {
            throw new TransitionNotFoundException(String.format("找不到这样的FsmTransition事件定义:%s,%s", state.getState(), event));
        }
        return worker;
    }


    public FsmFlow<S> local(S from, String event, Class<? extends LocalFsmAction> action, LocalRouter<S> router) {
        FsmWorker<S> sagaFsmWorker = FsmWorker.local(from, action, router);
        this.transitionTable.put(from, event, sagaFsmWorker);
        return this;
    }

    public FsmFlow<S> remote(S from, String event, Class<? extends RemoteFsmAction> action, RemoteRouter<S> router) {
        FsmWorker<S> sagaFsmWorker = FsmWorker.remote(from, action, router);
        this.transitionTable.put(from, event, sagaFsmWorker);
        return this;
    }


}
