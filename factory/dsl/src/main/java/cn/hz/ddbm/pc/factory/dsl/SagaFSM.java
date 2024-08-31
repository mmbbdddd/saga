//package cn.hz.ddbm.pc.factory.dsl;
//
//import cn.hutool.core.lang.Pair;
//import cn.hutool.core.map.multi.RowKeyTable;
//import cn.hutool.core.map.multi.Table;
//import cn.hz.ddbm.pc.common.lang.Triple;
//import cn.hz.ddbm.pc.core.*;
//import cn.hz.ddbm.pc.core.enums.FlowStatus;
//import cn.hz.ddbm.pc.core.processor.saga.SagaFsmHandler;
//import cn.hz.ddbm.pc.core.processor.saga.SagaState;
//import cn.hz.ddbm.pc.core.support.SessionManager;
//import cn.hz.ddbm.pc.core.support.StatusManager;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public interface SagaFSM<S extends Enum<S>> {
//    /**
//     * 状态机ID
//     *
//     * @return
//     */
//    String fsmId();
//
//    /**
//     * 状态机说明
//     *
//     * @return
//     */
//    String describe();
//
//    /**
//     * 定义插件，在每个节点执行前后执行。
//     * 常用的插件有日志插件，监控埋点插件……
//     *
//     * @return
//     */
//    List<Plugin> plugins();
//
//    /**
//     * 参见profile
//     *
//     * @return
//     */
//    SessionManager.Type session();
//
//    /**
//     * 参见profile
//     *
//     * @return
//     */
//    StatusManager.Type status();
//
//    /**
//     * 定义流程编排的各节点
//     * Map<节点，节点类型/>
//     *
//     * @return
//     */
//    List<Triple<S, SagaState.Offset, FlowStatus.Type>> nodes();
//
//    /**
//     * 定义混沌模式下，每个节点可能的状态值。
//     * Table<节点，事件,Set<Pair<目标节点,发生概率>>
//     *
//     * @return
//     */
//    Table<S, SagaState.Offset, Double> errorProbability();
//
//    /**
//     * 流程变迁设置，包含三种类型
//     * 事务业务：saga
//     * 非事务业务：to
//     * 查询业务：router
//     *
//     * @param
//     */
//    List<Triple<S, String, Integer>> pipeline();
//
//    /**
//     * 参见profile
//     *
//     * @return
//     */
//    Map<State, Profile.StateAttrs> stateAttrs();
//
//    /**
//     * 参见profile
//     */
//    Map<String, Profile.ActionAttrs> actionAttrs();
//
//    /**
//     * 流程的配置，例如状态管理，会话管理，缺省重试次数，超时事件，节点属性，atcion属性等
//     *
//     * @return
//     */
//    Profile profile();
//
//
//    default Fsm build() throws Exception {
//        Map<State, Profile.StateAttrs> stepAttrsMap = stateAttrs();
//        Profile                        profile      = profile();
//        profile.setStatusManager(status());
//        profile.setSessionManager(session());
//        profile.setActionAttrs(actionAttrs());
//        profile.setStateAttrs(stepAttrsMap);
//        profile.setPlugins(plugins());
//        Table<State, String, Set<Pair<State, Double>>> maybeResults = new RowKeyTable<>();
////        profile.setMaybeResults(maybeResults(maybeResults));
//        List<Triple<S, SagaState.Offset, FlowStatus.Type>> nodes    = nodes();
//        State                                              init     = getInitState(nodes);
//        Set<State>                                         ends     = getEndStates(nodes);
//        Set<State>                                         tasks    = getTaskStates(init, ends);
//        Fsm                                                fsm      = Fsm.of(fsmId(), describe(), init, tasks, ends, profile);
//        List<Triple<S, String, Integer>>                   pipeline = pipeline();
//        fsm.buildEventTable(buildToEventTable(pipeline));
////        InfraUtils.getBean(PcService.class).addFlow(flow);
//        return fsm;
//    }
//
//    default List<Transition> buildToEventTable(List<Triple<S, String, Integer>> pipeline) {
//        List<SagaFsmHandler> list = new ArrayList<>();
//        SagaState<S>         pre  = null;
//        for (int i = 0; i < pipeline.size(); i++) {
//            Triple<S, String, Integer> triple = pipeline.get(i);
//            SagaState<S>               task   = new SagaState<>(triple.getLeft());
//            SagaFsmHandler s = new SagaFsmHandler(
//                    task.task(), task.failover(), task.su(), task.rollback(), task.rollbackFailover(), null != pre ? pre.task() : null, triple.getMiddle()
//            );
//            list.add(s);
//            pre = task;
//        }
//        return list.stream().map(SagaFsmHandler::toTransitions).flatMap(Collection::stream).collect(Collectors.toList());
//    }
//
//    default State getInitState(List<Triple<S, SagaState.Offset, FlowStatus.Type>> nodes) {
//        return nodes.stream().filter(triple -> triple.getRight().equals(FlowStatus.Type.init)).map(tripe -> {
//            return new SagaState<>(tripe.getLeft(), tripe.getMiddle());
//        }).findFirst().get();
//    }
//
//    default Set<State> getTaskStates(State init, Set<State> ends) {
//        List<SagaState.Offset> offsets    = new ArrayList<>(EnumSet.allOf(SagaState.Offset.class));
//        List<S>                sagaStates = new ArrayList<>(EnumSet.allOf(stateType()));
//        Set<State> crossStates = offsets.stream()
//                .map(offset -> sagaStates.stream().map(sagaState -> new SagaState(sagaState, offset)).collect(Collectors.toList()))
//                .flatMap(Collection::stream)
//                .collect(Collectors.toSet());
//        crossStates.removeAll(ends);
//        crossStates.remove(init);
//        return crossStates;
//    }
//
//    Class<S> stateType();
//
//    default Set<State> getEndStates(List<Triple<S, SagaState.Offset, FlowStatus.Type>> nodes) {
//        return nodes.stream().filter(triple -> triple.getRight().equals(FlowStatus.Type.end)).map(tripe -> {
//            return new SagaState<>(tripe.getLeft(), tripe.getMiddle());
//        }).collect(Collectors.toSet());
//    }
//
//
//}
