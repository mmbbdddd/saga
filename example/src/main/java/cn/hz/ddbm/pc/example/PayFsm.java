package cn.hz.ddbm.pc.example;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.multi.Table;
import cn.hz.ddbm.pc.core.Plugin;
import cn.hz.ddbm.pc.core.Profile;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.enums.FlowStatus;
import cn.hz.ddbm.pc.core.support.SessionManager;
import cn.hz.ddbm.pc.core.support.StatusManager;
import cn.hz.ddbm.pc.core.utils.InfraUtils;
import cn.hz.ddbm.pc.factory.dsl.FSM;
import cn.hz.ddbm.pc.profile.PcService;
import org.mockito.internal.util.collections.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;


public class PayFsm implements FSM<PayState>, InitializingBean {
    Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public List<Plugin> plugins() {
        List<Plugin> plugins = new ArrayList<Plugin>();
//        plugins.add(new DigestLogPluginMock());
//        plugins.add(new PayAction());
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
     * @param table
     * @return
     */
    @Override
    public Table<PayState, String, Set<Pair<PayState, Double>>> maybeResults(Table<PayState, String, Set<Pair<PayState, Double>>> table) {
        table.put(PayState.init, Coasts.EVENT_DEFAULT, Sets.newSet(
                Pair.of(PayState.payed_failover, 0.1),
                Pair.of(PayState.payed_failover, 0.9)));
        table.put(PayState.payed_failover, Coasts.EVENT_DEFAULT, Sets.newSet(
                Pair.of(PayState.payed_failover, 0.1),
                Pair.of(PayState.init, 0.1),
                Pair.of(PayState.payed, 0.8)
        ));
        table.put(PayState.payed, Coasts.EVENT_DEFAULT, Sets.newSet(
                Pair.of(PayState.sended_failover, 0.1),
                Pair.of(PayState.su, 0.7),
                Pair.of(PayState.fail, 0.1),
                Pair.of(PayState.sended, 0.1)
        ));
        table.put(PayState.sended_failover, Coasts.EVENT_DEFAULT, Sets.newSet(
                Pair.of(PayState.sended_failover, 0.1),
                Pair.of(PayState.su, 0.7),
                Pair.of(PayState.fail, 0.1),
                Pair.of(PayState.sended, 0.1)
        ));
        table.put(PayState.sended, Coasts.EVENT_DEFAULT, Sets.newSet(
                Pair.of(PayState.sended_failover, 0.1),
                Pair.of(PayState.su, 0.7),
                Pair.of(PayState.fail, 0.1),
                Pair.of(PayState.sended, 0.1)
        ));
        return table;
    }

    @Override
    public Map<PayState, FlowStatus> nodes() {
        Map<PayState, FlowStatus> map = new HashMap<>();
        map.put(PayState.init, FlowStatus.INIT);
        map.put(PayState.payed, FlowStatus.RUNNABLE);
        map.put(PayState.sended, FlowStatus.RUNNABLE);
        map.put(PayState.payed_failover, FlowStatus.RUNNABLE);
        map.put(PayState.sended_failover, FlowStatus.RUNNABLE);
        map.put(PayState.su, FlowStatus.FINISH);
        map.put(PayState.fail, FlowStatus.FINISH);
        map.put(PayState.error, FlowStatus.FINISH);
        return map;
    }
    @Override
    public void transitions(Transitions<PayState> t) {
//        payAction:执行本地扣款
        t.saga(PayState.init, Coasts.EVENT_DEFAULT, Sets.newSet(PayState.init), PayState.payed_failover, "payAction")
                //本地扣款容错payQueryAction 扣款结果查询
                .router(PayState.payed_failover, Coasts.EVENT_DEFAULT, "payQueryAction")
                //发送异常，不明确是否发送
                .saga(PayState.payed, Coasts.EVENT_DEFAULT, Sets.newSet(PayState.payed), PayState.sended_failover, "sendAction")
                .router(PayState.sended_failover, Coasts.EVENT_DEFAULT, "sendQueryAction")
                //sendAction，执行远程发生&sendQueryAction。
                .router(PayState.sended, Coasts.EVENT_DEFAULT, "sendQueryAction");
    }


    @Override
    public Map<PayState, Profile.StepAttrs> stateAttrs() {
        return new HashMap<>();
    }

    @Override
    public Map<String, Profile.ActionAttrs> actionAttrs() {
        return new HashMap<>();
    }

    @Override
    public Profile<PayState> profile() {
        Profile<PayState> profile = new Profile(session(), status());
        profile.setRetry(2);
        return profile;
    }

    @Override
    public Map<PayState, String> cron() {
        return new HashMap<>();
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
        InfraUtils.getBean(PcService.class).addFlow(build());
    }
}
