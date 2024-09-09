package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.FlowModel;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.exception.FlowEndException;

import java.util.*;

public class SagaFlow<S extends Enum<S>> extends FlowModel<SagaState<S>> {
    Pipeline<S> pipeline;

    public SagaFlow(String name, List<Pair<S, Class<? extends SagaAction>>> chains) {
        Assert.notNull(chains, "chains is null");
        Assert.notNull(name, "name is null");
        this.name = name;
        pipeline  = new Pipeline<>(chains, chains.size());
    }

    @Override
    public boolean isEnd(SagaState<S> state) {
        return pipeline.isSuccessOrFail(state.index);

    }
    public SagaWorker<S> getWorker(Integer index, FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws FlowEndException {
        return pipeline.get(index, ctx);
    }

}

class Pipeline<S extends Enum<S>> {
    List<Pair<S, SagaWorker<S>>> list;

    public Pipeline(List<Pair<S, Class<? extends SagaAction>>> chains, Integer total) {
        this.list = new ArrayList<>();
        this.list.add(SagaWorker.failWorker());
        for (int i = 0; i < chains.size(); i++) {
            Pair<S, Class<? extends SagaAction>> node = chains.get(i);
            this.list.add(Pair.of(node.getKey(), SagaWorker.of(i, node, total)));
        }
        this.list.add(SagaWorker.successWorker());
    }

    public SagaWorker<S> get(Integer index, FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws FlowEndException {
        if (isSuccessOrFail(index)) {
            throw new FlowEndException();
        }
        return list.stream().filter(f -> f.getValue().index.equals(index)).findFirst().get().getValue();
    }

    public boolean isSuccessOrFail(Integer index) {
        return Objects.equals(index, 0) || Objects.equals(list.size() - 1, index);
    }
}
