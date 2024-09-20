package cn.hz.ddbm.pc;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.newcore.*;
import cn.hz.ddbm.pc.newcore.chaos.LocalChaosAction;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.LockException;
import cn.hz.ddbm.pc.newcore.exception.SessionException;
import cn.hz.ddbm.pc.newcore.exception.StatusException;
import cn.hz.ddbm.pc.newcore.fsm.actions.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.infra.*;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmLocker;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmSessionManager;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmStatisticsSupport;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmStatusManager;
import cn.hz.ddbm.pc.newcore.infra.proxy.*;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.saga.actions.LocalSagaAction;
import cn.hz.ddbm.pc.newcore.utils.EnvUtils;
import com.oracle.webservices.internal.api.EnvelopeStyle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProcesorService<S extends State> {
    protected Map<String, BaseFlow> flows;
    Map<Coast.SessionType, SessionManager>       sessionManagerMap;
    Map<Coast.StatusType, StatusManager>         statusManagerMap;
    Map<Coast.LockType, Locker>                  lockerMap;
    Map<Coast.ScheduleType, ScheduleManger>      scheduleMangerMap;
    Map<Coast.StatisticsType, StatisticsSupport> statisticsSupportMap;

    PluginService pluginService;

    public ProcesorService() {
        this.flows                = new HashMap<>();
        this.sessionManagerMap    = new HashMap<>();
        this.statusManagerMap     = new HashMap<>();
        this.lockerMap            = new HashMap<>();
        this.scheduleMangerMap    = new HashMap<>();
        this.statisticsSupportMap = new HashMap<>();

        this.pluginService = new PluginService();

        this.statisticsSupportMap.put(Coast.StatisticsType.jvm, new JvmStatisticsSupport());
        this.sessionManagerMap.put(Coast.SessionType.jvm, new JvmSessionManager());
        this.statusManagerMap.put(Coast.StatusType.jvm, new JvmStatusManager());
//        this.scheduleMangerMap.put(Coast.ScheduleType.timer, new TimerScheduleManager());
        this.lockerMap.put(Coast.LockType.jvm, new JvmLocker());
    }


    public void initParent() {
        SpringUtil.getBeansOfType(SessionManager.class).forEach((key, bean) -> {
            this.sessionManagerMap.put(bean.code(), new SessionManagerProxy(bean));
        });
        SpringUtil.getBeansOfType(StatusManager.class).forEach((key, bean) -> {
            this.statusManagerMap.put(bean.code(), new StatusManagerProxy(bean));
        });
        SpringUtil.getBeansOfType(Locker.class).forEach((key, bean) -> {
            this.lockerMap.put(bean.code(), new LockProxy(bean));
        });
        SpringUtil.getBeansOfType(ScheduleManger.class).forEach((key, bean) -> {
            this.scheduleMangerMap.put(bean.code(), new ScheduleMangerProxy(bean));
        });
        SpringUtil.getBeansOfType(StatisticsSupport.class).forEach((key, bean) -> {
            this.statisticsSupportMap.put(bean.code(), new StatisticsSupportProxy(bean));
        });
    }


    public <F> F getFlow(String flowName) {
        return (F) flows.get(flowName);
    }

    public PluginService plugin() {
        return pluginService;
    }

    public Map<String, Object> getSession(String flowName, Serializable id) throws SessionException {
        BaseFlow flow    = getFlow(flowName);
        Profile  profile = flow.getProfile();
        return sessionManagerMap.get(profile.getSession()).get(flowName, id);
    }


    public void metricsNode(FlowContext<S> ctx) {
        String            flowName = ctx.getFlow().getName();
        Serializable      id       = ctx.getId();
        State             state    = ctx.getState();
        StatisticsSupport ss       = statisticsSupportMap.get(ctx.getProfile().getStatistics());
    }

    public Long getExecuteTimes(FlowContext<S> ctx) {
        String            flowName = ctx.getFlow().getName();
        Serializable      id       = ctx.getId();
        State             state    = ctx.getState();
        StatisticsSupport ss       = statisticsSupportMap.get(ctx.getProfile().getStatistics());
        return ss.get(flowName, id, state, Coast.STATISTICS.EXECUTE_TIMES);
    }

    public static <T> T getAction(Class<T> action) {
        Assert.notNull(action, "action is null");
        if (EnvUtils.isChaos()) {
            if (LocalFsmAction.class.isAssignableFrom(action) || LocalSagaAction.class.isAssignableFrom(action)) {
                return (T)SpringUtil.getBean(LocalChaosAction.class);
            } else {
                return SpringUtil.getBean(Coast.REMOTE_CHAOS_ACTION);
            }
        } else {
            return SpringUtil.getBean(action);
        }
    }

}


