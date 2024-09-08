package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.FlowModel;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.exception.FlowEndException;

import java.util.*;

public class SagaFlow<S extends Enum<S>> extends FlowModel<SagaState<S>> {
    public Pipeline<S> pipeline;

    public SagaFlow(String name, List<Pair<S, Class<? extends SagaAction>>> chains) {
        Assert.notNull(chains, "chains is null");
        Assert.notNull(name, "name is null");
        this.name      = name;
        this.init      = buildInit(chains);
        this.ends      = buildEnds(chains);
        this.tasks     = buildTasks(chains);
        this.allStates = new HashSet<>();
        this.allStates.add(init);
        this.allStates.addAll(ends);
        this.allStates.addAll(tasks);
        pipeline = new Pipeline<>(chains, chains.size());
    }

    private static <S extends Enum<S>> SagaState<S> buildInit(List<Pair<S, Class<? extends SagaAction>>> lines) {
        Assert.notNull(lines, "lines is null");
        return new SagaState<>(0, SagaState.Offset.task, FlowStatus.RUNNABLE);
    }

    private static <S extends Enum<S>> Set<SagaState<S>> buildEnds(List<Pair<S, Class<? extends SagaAction>>> lines) {
        Assert.notNull(lines, "tasks is null");
        Set<SagaState<S>> ends = new HashSet<>();
        //最后一个节点，状态为成功，为结束节点
        ends.add(new SagaState<>(lines.size() - 1, null, FlowStatus.SU));
        //初始化，并且状态是fail。为结束节点
        ends.add(new SagaState<>(0, null, FlowStatus.FAIL));
        return ends;
    }

    private static <S extends Enum<S>> Set<SagaState<S>> buildTasks(List<Pair<S, Class<? extends SagaAction>>> lines) {
        List<SagaState<S>> all = new ArrayList<>();
        for (int index = 0; index < lines.size(); index++) {
            int finalIndex = index;
            EnumSet.allOf(SagaState.Offset.class).forEach(offset -> {
                all.add(new SagaState<>(finalIndex, offset, FlowStatus.RUNNABLE));
            });
        }
        all.remove(buildInit(lines));
        all.removeAll(buildEnds(lines));
        return new HashSet<>(all);
    }

    @Override
    public boolean isEnd(SagaState<S> state) {
        return FlowStatus.isEnd(state.getStatus());

    }


    public SagaWorker<S> getWorker(Integer index, FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws FlowEndException {
        return pipeline.get(index,ctx);
    }


}

class Pipeline<S extends Enum<S>> {
    List<Pair<S, SagaWorker<S>>> list;

    public Pipeline(List<Pair<S, Class<? extends SagaAction>>> chains, Integer total) {
        this.list = new ArrayList<>();
        for (int i = 0; i < chains.size(); i++) {
            Pair<S, Class<? extends SagaAction>> node = chains.get(i);
            this.list.add(Pair.of(node.getKey(), SagaWorker.of(i, node, total)));
        }
    }

    public SagaWorker<S> get(Integer index, FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws FlowEndException {
        return list.stream().filter(f -> f.getValue().index.equals(index)).findFirst().orElseThrow(() -> {
            if (index == 0) {
                ctx.getState().setStatus(FlowStatus.FAIL);
            } else {
                ctx.getState().setStatus(FlowStatus.SU);
            }
            return new FlowEndException();
        }).getValue();
    }
}
