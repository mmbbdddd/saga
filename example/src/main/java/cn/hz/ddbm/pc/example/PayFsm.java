package cn.hz.ddbm.pc.example;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.factory.fsm.FSM;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.plugin.PerformancePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static cn.hz.ddbm.pc.example.PayState.*;


public class PayFsm implements FSM<PayState> {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    PerformancePlugin performancePlugin;

    @Override
    public List<Plugin> plugins(List<Plugin> plugins) {
//        plugins.add(new DigestLogPluginMock());
//        plugins.add(new PayAction());
//        plugins.add(performancePlugin);
//        plugins.add(new PayQueryAction());
//        plugins.add(new SendAction());
//        plugins.add(new SendQueryAction());
        return plugins;
    }

    @Override
    public Coast.SessionType session() {
        return Coast.SessionType.jvm;
    }

    @Override
    public Coast.StatusType status() {
        return Coast.StatusType.jvm;
    }

    @Override
    public List<Pair<PayState, FlowStatus.Type>> nodes(List<Pair<PayState, FlowStatus.Type>> list) {
        list.add(Pair.of(init,FlowStatus.Type.init));
        list.add(Pair.of(su,FlowStatus.Type.end));
        list.add(Pair.of(fail,FlowStatus.Type.end));
        return list;
    }

    @Override
    public Transitions<PayState> transitions(Transitions<PayState> t) {
        return t.to(init,Coast.FSM.EVENT_DEFAULT,"", PayState.freezed)
                .router(PayState.freezed,Coast.FSM.EVENT_DEFAULT,"sendAction", PayState.sendfailover);
    }


    /**
     * 定义混沌模式下
     *
     * @return
     */

//    @Override
//    public List<Triple<PayState, SagaState.Offset, FlowStatus.Type>> nodes() {
//        List<Triple<PayState, SagaState.Offset, FlowStatus.Type>> nodeTypes = new ArrayList<>();
//        nodeTypes.add(Triple.of(PayState.init, SagaState.Offset.task, FlowStatus.Type.init));
//        nodeTypes.add(Triple.of(PayState.payed, SagaState.Offset.su, FlowStatus.Type.end));
//        nodeTypes.add(Triple.of(PayState.init, SagaState.Offset.rollback, FlowStatus.Type.end));
//        return nodeTypes;
//    }

//    @Override
//    public Table<PayState, SagaState.Offset, Double> errorProbability() {
//        Table<PayState, SagaState.Offset, Double> table = new RowKeyTable<>();
//        EnumSet.allOf(stateType()).forEach(sagaState -> {
//            table.put(sagaState, SagaState.Offset.failover, 0.1);
//            table.put(sagaState, SagaState.Offset.su, 0.8);
//            table.put(sagaState, SagaState.Offset.rollback, 0.1);
//        });
//        return table;
//    }

//    @Override
//    public List<Triple<PayState, String, Integer>> pipeline() {
//        List<Triple<PayState, String, Integer>> line = new ArrayList<>();
//        line.add(Triple.of(PayState.init, "freezedAction", 10));
//        line.add(Triple.of(PayState.freezed, "sendAction", 10));
//        line.add(Triple.of(PayState.sended, "payAction", 10));
//        line.add(Triple.of(PayState.payed, "", 10));
//        return line;
//    }

    @Override
    public Profile profile() {
        return Profile.devOf();
    }


    @Override
    public Class<PayState> type() {
        return PayState.class;
    }


//    @Override
//    public Map<State, Profile.StateAttrs> stateAttrs() {
//        return new HashMap<>();
//    }
//
//    @Override
//    public Map<String, Profile.ActionAttrs> actionAttrs() {
//        return new HashMap<>();
//    }

//    @Override
//    public Profile profile() {
//        Profile profile = new Profile(session(), status());
//        profile.setRetry(20);
//        return profile;
//    }

//    @Override
//    public Class<PayState> stateType() {
//        return PayState.class;
//    }


    public String fsmId() {
        return "test";
    }

    @Override
    public String describe() {
        return "test";
    }

}
