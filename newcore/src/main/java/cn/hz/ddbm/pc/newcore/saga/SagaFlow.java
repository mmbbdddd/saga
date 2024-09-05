package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.FlowModel;
import cn.hz.ddbm.pc.newcore.exception.FlowEndException;

import java.util.*;
import java.util.stream.Collectors;

public class SagaFlow<S extends Enum<S>> extends FlowModel<SagaState<S>> {
    Map<S, SagaWorker<S>> pipeline;


    public SagaFlow(String name, List<Pair<S, Class<? extends SagaAction>>> pairs) {
       this(name, parseTasks(pairs), parseActions(pairs));
    }

    private static <S extends Enum<S>> List<Class<? extends SagaAction>> parseActions(List<Pair<S, Class<? extends SagaAction>>> pairs) {
        Assert.notNull(pairs,"pair is null");
        return pairs.stream().map(Pair::getValue).collect(Collectors.toList());
    }

    private static <S extends Enum<S>> List<S> parseTasks(List<Pair<S, Class<? extends SagaAction>>> pairs) {
        Assert.notNull(pairs,"pair is null");
        return pairs.stream().map(Pair::getKey).collect(Collectors.toList());
    }

    private SagaFlow(String name, List<S> tasks, List<Class<? extends SagaAction>> actions) {
        super(name, buildInit(tasks), buildEnds(tasks), buildTasks(tasks));

        pipeline = new HashMap<>();
        for (int i = 0; i < tasks.size(); i++) {
            SagaWorker<S> worker = new SagaWorker<>(i, getPre(tasks, i), getCurrent(tasks, i), getNext(tasks, i), actions.get(i));
            pipeline.put(worker.getCurrentState(), worker);
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


    public boolean isEnd(SagaState<?> state) {
        return getEnds().contains(state);
    }

    public SagaWorker<S> getWorker(S state) throws FlowEndException {
        return pipeline.get(state);
    }


    private static <S extends Enum<S>> SagaState<S> buildInit(List<S> tasks) {
        Assert.notNull(tasks,"tasks is null");
        return new SagaState<>(tasks.get(0), SagaState.Offset.task, true);
    }

    private static <S extends Enum<S>> Set<SagaState<S>> buildEnds(List<S> tasks) {
        Assert.notNull(tasks,"tasks is null");
        Set<SagaState<S>> ends = new HashSet<>();
        //初始化，并且状态是fail。为结束节点
        ends.add(new SagaState<>(tasks.get(0), SagaState.Offset.fail, false));
        //最后一个节点，状态为成功，为结束节点
        ends.add(new SagaState<>(tasks.get(tasks.size() - 1), SagaState.Offset.su, true));
        return ends;
    }

    private static <S extends Enum<S>> Set<SagaState<S>> buildTasks(List<S> tasks) {
        List<SagaState<S>> forwards = tasks.stream()
                .map(state -> EnumSet.allOf(SagaState.Offset.class).stream().map(offset -> new SagaState<S>(state, offset, true)).collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<SagaState<S>> backoff = tasks.stream()
                .map(state -> EnumSet.allOf(SagaState.Offset.class).stream().map(offset -> new SagaState<S>(state, offset, false)).collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<SagaState<S>> all = new ArrayList<>();
        all.addAll(forwards);
        all.addAll(backoff);
        all.remove(buildInit(tasks));
        all.remove(buildEnds(tasks));
        return new HashSet<>(all);
    }

}
