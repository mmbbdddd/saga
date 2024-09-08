package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.FlowModel;
import cn.hz.ddbm.pc.newcore.exception.FlowEndException;

import java.util.*;

public class SagaFlow<S extends Enum<S>> extends FlowModel<SagaState<S>> {
    Pipeline<S> pipeline;

    public SagaFlow(String name, List<Pair<S, Class<? extends SagaAction>>> chains) {
        Assert.notNull(chains, "chains is null");
        Assert.notNull(name, "name is null");
        this.name      = name;
        this.init      = buildInit(chains, this);
        this.ends      = buildEnds(chains, this);
        this.tasks     = buildTasks(chains, this);
        this.allStates = new HashSet<>();
        this.allStates.add(init);
        this.allStates.addAll(ends);
        this.allStates.addAll(tasks);
        pipeline = new Pipeline<>(chains, this);
    }

    private static <S extends Enum<S>> SagaState<S> buildInit(List<Pair<S, Class<? extends SagaAction>>> lines, SagaFlow<S> flow) {
        Assert.notNull(lines, "lines is null");
        return new SagaState<>(0, SagaState.Offset.task, flow);
    }

    private static <S extends Enum<S>> Set<SagaState<S>> buildEnds(List<Pair<S, Class<? extends SagaAction>>> lines, SagaFlow<S> flow) {
        Assert.notNull(lines, "tasks is null");
        Set<SagaState<S>> ends = new HashSet<>();
        //最后一个节点，状态为成功，为结束节点
        ends.add(new SagaState<>(null, null, flow));
        //初始化，并且状态是fail。为结束节点
        ends.add(new SagaState<>(null, null, flow));
        return ends;
    }

    private static <S extends Enum<S>> Set<SagaState<S>> buildTasks(List<Pair<S, Class<? extends SagaAction>>> lines, SagaFlow<S> flow) {
        List<SagaState<S>> all = new ArrayList<>();
        for (int index = 0; index < lines.size(); index++) {
            int finalIndex = index;
            EnumSet.allOf(SagaState.Offset.class).forEach(offset -> {
                all.add(new SagaState<>(finalIndex, offset, flow));
            });
        }
        all.remove(buildInit(lines, flow));
        all.removeAll(buildEnds(lines, flow));
        return new HashSet<>(all);
    }

    @Override
    public boolean isEnd(SagaState<S> state) {
        return getEnds().contains(state);
    }


    public SagaWorker<S> getWorker(Integer index) throws FlowEndException {
        return pipeline.get(index);
    }
}

class Pipeline<S extends Enum<S>> {
    List<Pair<S, SagaWorker<S>>> list;

    public Pipeline(List<Pair<S, Class<? extends SagaAction>>> chains, SagaFlow<S> flow) {
        this.list = new ArrayList<>();
        for (int i = 0; i < chains.size(); i++) {
            Pair<S, Class<? extends SagaAction>> node = chains.get(i);
            this.list.add(Pair.of(node.getKey(), SagaWorker.of(i, node, flow)));
        }
    }

    public SagaWorker<S> get(Integer index) throws FlowEndException {
        return list.stream().filter(f -> f.getValue().index.equals(index)).findFirst().orElseThrow(() -> new FlowEndException()).getValue();
    }
}
