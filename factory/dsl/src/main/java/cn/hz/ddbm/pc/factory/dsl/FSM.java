package cn.hz.ddbm.pc.factory.dsl;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import cn.hz.ddbm.pc.core.Fsm;
import cn.hz.ddbm.pc.core.Plugin;
import cn.hz.ddbm.pc.core.Profile;
import cn.hz.ddbm.pc.core.Transition;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.enums.FlowStatus;
import cn.hz.ddbm.pc.core.support.SessionManager;
import cn.hz.ddbm.pc.core.support.StatusManager;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface FSM<S extends Enum<S>> {
    /**
     * 状态机ID
     *
     * @return
     */
    String fsmId();

    /**
     * 状态机说明
     *
     * @return
     */
    String describe();

    /**
     * 定义插件，在每个节点执行前后执行。
     * 常用的插件有日志插件，监控埋点插件……
     *
     * @return
     */
    List<Plugin> plugins();

    /**
     * 参见profile
     *
     * @return
     */
    SessionManager.Type session();

    /**
     * 参见profile
     *
     * @return
     */
    StatusManager.Type status();

    /**
     * 定义流程编排的各节点
     * Map<节点，节点类型/>
     *
     * @return
     */
    Map<S, FlowStatus> nodes();

    /**
     * 定义混沌模式下，每个节点可能的状态值。
     * Table<节点，事件,Set<Pair<目标节点,发生概率>>
     *
     * @param table
     * @return
     */
    Table<S, String, Set<Pair<S, Double>>> maybeResults(Table<S, String, Set<Pair<S, Double>>> table);

    /**
     * 流程变迁设置，包含三种类型
     * 事务业务：saga
     * 非事务业务：to
     * 查询业务：router
     *
     * @param eventTable
     */
    void transitions(Fsm.EventTable<S> eventTable);

    /**
     * 参见profile
     *
     * @return
     */
    Map<S, Profile.StepAttrs> stateAttrs();

    /**
     * 参见profile
     */
    Map<String, Profile.ActionAttrs> actionAttrs();

    /**
     * 流程的配置，例如状态管理，会话管理，缺省重试次数，超时事件，节点属性，atcion属性等
     *
     * @return
     */
    Profile<S> profile();


    default Fsm<S> build() throws Exception {
        Map<S, Profile.StepAttrs> stepAttrsMap = stateAttrs();
        Profile<S>                profile      = profile();
        profile.setStatusManager(status());
        profile.setSessionManager(session());
        profile.setActions(actionAttrs());
        profile.setStates(stepAttrsMap);
        profile.setPlugins(plugins());
        Table<S, String, Set<Pair<S, Double>>> maybeResults = new RowKeyTable<>();
        profile.setMaybeResults(maybeResults(maybeResults));
        Map<S, FlowStatus> nodes = nodes();
        S                  init  = nodes.entrySet().stream().filter(e -> e.getValue().equals(FlowStatus.INIT)).map(Map.Entry::getKey).findFirst().get();
        Set<S> tasks = nodes.entrySet()
                .stream()
                .filter(e -> FlowStatus.isRunnable(e.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        Set<S> ends = nodes.entrySet().stream().filter(e -> FlowStatus.isEnd(e.getValue())).map(Map.Entry::getKey).collect(Collectors.toSet());
        Fsm<S> fsm  = Fsm.of(fsmId(), describe(), init, tasks, ends, profile);
        transitions(fsm.getEventTable());
//        InfraUtils.getBean(PcService.class).addFlow(flow);
        return fsm;
    }


}
