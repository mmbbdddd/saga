package cn.hz.ddbm.pc.newcore.fsm;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import cn.hz.ddbm.pc.newcore.FlowModel;
import cn.hz.ddbm.pc.newcore.exception.TransitionNotFoundException;
import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.router.LocalRouter;
import cn.hz.ddbm.pc.newcore.fsm.router.RemoteRouter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FsmFlow<S extends Enum<S>> extends FlowModel<FsmState<S>> {
    Table<S, String, FsmWorker<S>> transitionTable;

    public FsmFlow(String name, S init, Set<S> ends, Set<S> tasks) {
        Assert.notNull(name, "name is null");
        Assert.notNull(init, "init is null");
        Assert.notNull(ends, "ends null");
        Assert.notNull(tasks, "tasks is null");
        this.name      = name;
        this.init      = new FsmState<>(init, FsmState.Offset.task);
        this.ends      = ends.stream().map(e -> new FsmState<>(e, FsmState.Offset.task)).collect(Collectors.toSet());
        this.tasks     = tasks.stream().map(e -> new FsmState<>(e, FsmState.Offset.task)).collect(Collectors.toSet());
        this.allStates = new HashSet<>();
        this.allStates.add(this.init);
        this.allStates.addAll(this.ends);
        this.allStates.addAll(this.tasks);
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


    @Override
    public boolean isEnd(FsmState<S> state) {
        return getEnds().stream().map(e->e.getState()).collect(Collectors.toSet()).contains(state.getState());
    }

}
