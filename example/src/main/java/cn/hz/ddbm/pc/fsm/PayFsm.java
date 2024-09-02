package cn.hz.ddbm.pc.fsm;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.actions.fsm.FreezedAction;
import cn.hz.ddbm.pc.actions.fsm.SendAction;
import cn.hz.ddbm.pc.factory.fsm.FSM;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.plugin.PerformancePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static cn.hz.ddbm.pc.fsm.PayState.*;


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
        list.add(Pair.of(init, FlowStatus.Type.init));
        list.add(Pair.of(su, FlowStatus.Type.end));
        list.add(Pair.of(fail, FlowStatus.Type.end));
        return list;
    }

    @Override
    public void transitions(Transitions<PayState> transitions) {
        transitions.state(init)
                .onEventRouter(Coast.FSM.EVENT_DEFAULT, FreezedAction.class, freezed)
                .endState()
                .state(freezed)
                .onEventRouter(Coast.FSM.EVENT_DEFAULT, SendAction.class, sendfailover)
                .endState();
    }


    @Override
    public Profile profile() {
        return Profile.devOf();
    }


    @Override
    public Class<PayState> type() {
        return PayState.class;
    }


    public String fsmId() {
        return "test";
    }

    @Override
    public String describe() {
        return "test";
    }

}
