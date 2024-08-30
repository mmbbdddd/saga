package cn.hz.ddbm.pc.example;

import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.core.*;
import cn.hz.ddbm.pc.core.enums.FlowStatus;
import cn.hz.ddbm.pc.core.processor.saga.SagaState;
import cn.hz.ddbm.pc.core.processor.saga.SagaTransitionBuilder;
import cn.hz.ddbm.pc.core.support.SessionManager;
import cn.hz.ddbm.pc.core.support.StatusManager;
import cn.hz.ddbm.pc.core.utils.InfraUtils;
import cn.hz.ddbm.pc.factory.dsl.SagaFSM;
import cn.hz.ddbm.pc.plugin.PerformancePlugin;
import cn.hz.ddbm.pc.profile.BaseService;
import cn.hz.ddbm.pc.support.DigestLogPluginMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


public class PayFsm implements SagaFSM<PayState>, InitializingBean {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    PerformancePlugin performancePlugin;

    @Override
    public List<Plugin> plugins() {
        List<Plugin> plugins = new ArrayList<Plugin>();
        plugins.add(new DigestLogPluginMock());
//        plugins.add(new PayAction());
        plugins.add(performancePlugin);
//        plugins.add(new PayQueryAction());
//        plugins.add(new SendAction());
//        plugins.add(new SendQueryAction());
        return plugins;
    }

    @Override
    public SessionManager.Type session() {
        return SessionManager.Type.memory;
    }

    @Override
    public StatusManager.Type status() {
        return StatusManager.Type.memory;
    }


    /**
     * 定义混沌模式下
     *
     * @return
     */

    @Override
    public List<Triple<PayState, SagaState.Offset, FlowStatus.Type>> nodes() {
        List<Triple<PayState, SagaState.Offset, FlowStatus.Type>> nodeTypes = new ArrayList<>();
        nodeTypes.add(Triple.of(PayState.init, SagaState.Offset.task, FlowStatus.Type.init));
        nodeTypes.add(Triple.of(PayState.payed, SagaState.Offset.su, FlowStatus.Type.end));
        nodeTypes.add(Triple.of(PayState.init, SagaState.Offset.rollback, FlowStatus.Type.end));
        return nodeTypes;
    }

    @Override
    public Table<PayState, SagaState.Offset, Double> errorProbability() {
        Table<PayState, SagaState.Offset, Double> table = new RowKeyTable<>();
        EnumSet.allOf(stateType()).forEach(sagaState -> {
            table.put(sagaState, SagaState.Offset.failover, 0.1);
            table.put(sagaState, SagaState.Offset.su, 0.8);
            table.put(sagaState, SagaState.Offset.rollback, 0.1);
        });
        return table;
    }

    @Override
    public List<Triple<PayState, String, Integer>> pipeline() {
        List<Triple<PayState, String, Integer>> line = new ArrayList<>();
        line.add(Triple.of(PayState.init, "freezedAction", 10));
        line.add(Triple.of(PayState.freezed, "sendAction", 10));
        line.add(Triple.of(PayState.sended, "payAction", 10));
        line.add(Triple.of(PayState.payed, "", 10));
        return line;
    }


    @Override
    public Map<State, Profile.StateAttrs> stateAttrs() {
        return new HashMap<>();
    }

    @Override
    public Map<String, Profile.ActionAttrs> actionAttrs() {
        return new HashMap<>();
    }

    @Override
    public Profile profile() {
        Profile profile = new Profile(session(), status());
        profile.setRetry(20);
        return profile;
    }

    @Override
    public Class<PayState> stateType() {
        return PayState.class;
    }


    public String fsmId() {
        return "test";
    }

    @Override
    public String describe() {
        return "test";
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        InfraUtils.getBean(BaseService.class).addFlow(build());
    }
}
