package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.FlowModel;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.exception.FlowEndException;

import java.util.*;

public class SagaFlow<S extends Enum<S>> extends FlowModel<SagaState<S>> {
    List<Pair<S, SagaWorker<S>>> list;

    public SagaFlow(String name, List<Pair<S, Class<? extends SagaAction>>> chains) {
        Assert.notNull(chains, "chains is null");
        Assert.notNull(name, "name is null");
        this.name = name;
        this.list = new ArrayList<>();
        this.list.add(failSagaWorkerPair());
        for (int i = 0; i < chains.size(); i++) {
            Pair<S, Class<? extends SagaAction>> node = chains.get(i);
            this.list.add(Pair.of(node.getKey(), SagaWorker.of(i, node, chains.size())));
        }
        this.list.add(successSagaWorkerPair());
    }

    @Override
    public boolean isEnd(SagaState<S> state) {
        return Objects.equals(state.index, 0) || Objects.equals(list.size() - 1, state.index);

    }

    public SagaWorker<S> getWorker(Integer index)   {
        return list.stream().filter(f -> f.getValue().index.equals(index)).findFirst().get().getValue();
    }


    public boolean isSuccessOrFail(Integer index) {
        return Objects.equals(index, 0) || Objects.equals(list.size() - 1, index);
    }

    private Pair<S, SagaWorker<S>> failSagaWorkerPair() {
        return Pair.of(null,SagaWorker.failWorker());
    }

    private Pair<S, SagaWorker<S>> successSagaWorkerPair() {
        return Pair.of(null,SagaWorker.successWorker());
    }

}


