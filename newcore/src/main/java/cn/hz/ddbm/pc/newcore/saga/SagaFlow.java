package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.FlowModel;
import cn.hz.ddbm.pc.newcore.OffsetState;
import cn.hz.ddbm.pc.newcore.exception.FlowEndException;
import cn.hz.ddbm.pc.newcore.saga.action.LocalSagaAction;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SagaFlow<S extends Enum<S>> extends FlowModel<SagaState<S>> {
    Map<S, SagaWorker<S>> pipeline;


    public SagaFlow(String name, List<Pair<S, Class<? extends Action>>> pairs) {
        this(name, parseTasks(pairs), parseActions(pairs));
    }

    private static <S extends Enum<S>> List<Class<? extends Action>> parseActions(List<Pair<S, Class<? extends Action>>> pairs) {
        Assert.notNull(pairs, "pair is null");
        return pairs.stream().map(Pair::getValue).collect(Collectors.toList());
    }

    private static <S extends Enum<S>> List<S> parseTasks(List<Pair<S, Class<? extends Action>>> pairs) {
        Assert.notNull(pairs, "pair is null");
        return pairs.stream().map(Pair::getKey).collect(Collectors.toList());
    }

    private SagaFlow(String name, List<S> tasks, List<Class<? extends Action>> actions) {
        super(name, buildInit(tasks), buildEnds(tasks), buildTasks(tasks));

        pipeline = new HashMap<>();
        for (int i = 0; i < tasks.size(); i++) {
//            SagaWorker<S> worker = new SagaWorker<>(i, getPre(tasks, i), getCurrent(tasks, i), getNext(tasks, i), actions.get(i));
            Class action = actions.get(i);
            if (LocalSagaAction.class.isAssignableFrom(actions.get(i))) {
                SagaWorker<S> worker = SagaWorker.local(i, getPre(tasks, i), getCurrent(tasks, i), getNext(tasks, i), action);
                pipeline.put(worker.getState(), worker);
            } else {
                SagaWorker<S> worker = SagaWorker.remote(i, getPre(tasks, i), getCurrent(tasks, i), getNext(tasks, i), action);
                pipeline.put(worker.getState(), worker);
            }
        }
    }

    private S getPre(List<S> tasks, int i) {
        if (i == 0) return null;
        return tasks.get(i - 1);
    }

    private S getCurrent(List<S> tasks, int i) {
        return tasks.get(i);
    }

    private S getNext(List<S> tasks, int i) {
        if (i > (tasks.size() - 2)) return null;
        return tasks.get(i + 1);
    }


    public SagaWorker<S> getWorker(S state) throws FlowEndException {
        return pipeline.get(state);
    }


    private static <S extends Enum<S>> SagaState<S> buildInit(List<S> tasks) {
        Assert.notNull(tasks, "tasks is null");
        return new SagaState<>(tasks.get(0), OffsetState.init, SagaState.Direction.forward);
    }

    private static <S extends Enum<S>> Set<SagaState<S>> buildEnds(List<S> tasks) {
        Assert.notNull(tasks, "tasks is null");
        Set<SagaState<S>> ends = new HashSet<>();
        //初始化，并且状态是fail。为结束节点
        ends.add(new SagaState<>(tasks.get(0), OffsetState.end, SagaState.Direction.backoff));
        //最后一个节点，状态为成功，为结束节点
        ends.add(new SagaState<>(tasks.get(tasks.size() - 1), OffsetState.end, SagaState.Direction.forward));
        return ends;
    }

    private static <S extends Enum<S>> Set<SagaState<S>> buildTasks(List<S> tasks) {
        List<SagaState<S>> forwards = tasks.stream()
                .map(state -> OffsetState.runnables()
                        .stream()
                        .map(offset -> new SagaState<S>(state, offset, SagaState.Direction.forward))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<SagaState<S>> backoff = tasks.stream()
                .map(state -> OffsetState.runnables()
                        .stream()
                        .map(offset -> new SagaState<S>(state, offset, SagaState.Direction.backoff))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<SagaState<S>> all = new ArrayList<>();
        all.addAll(forwards);
        all.addAll(backoff);
        return new HashSet<>(all);
    }

    @Override
    public boolean isEnd(SagaState<S> state) {
        return getEnds().contains(state);
    }




}
