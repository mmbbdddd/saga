package cn.hz.ddbm.pc;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.newcore.*;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.LockException;
import cn.hz.ddbm.pc.newcore.exception.SessionException;
import cn.hz.ddbm.pc.newcore.exception.StatusException;
import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.infra.*;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmLocker;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmSessionManager;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmStatisticsSupport;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmStatusManager;
import cn.hz.ddbm.pc.newcore.infra.proxy.*;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.saga.action.LocalSagaAction;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class ProcesorService<S extends State, C extends FlowContext<?, S, ?>> implements FlowProcessor<C> {
    protected Map<String, FlowModel> flows;
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

        this.pluginService = new PluginService(getDefaultPlugins());

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

    public abstract C getContext(String flowName, Payload<S> payload) throws SessionException;

    protected abstract List<Plugin> getDefaultPlugins();

    public <F> F getFlow(String flowName) {
        return (F) flows.get(flowName);
    }

    public PluginService plugin() {
        return pluginService;
    }

    public Map<String, Object> getSession(String flowName, Serializable id) throws SessionException {
        FlowModel flow    = getFlow(flowName);
        Profile   profile = flow.getProfile();
        return sessionManagerMap.get(profile.getSession()).get(flowName, id);
    }



    public void updateStatus(C ctx) throws StatusException {
        ctx.syncpayload();
        statusManagerMap.get(ctx.getProfile().getStatus()).flush(ctx);
    }

    /**
     * 刷新状态到基础设施
     */
    public void flush(C ctx) throws SessionException, StatusException {
        ctx.syncpayload();
        sessionManagerMap.get(ctx.getProfile().getSession()).flush(ctx);
        statusManagerMap.get(ctx.getProfile().getStatus()).flush(ctx);
    }

    public void idempotent(C ctx) throws IdempotentException {
        String namespace = String.format("idempotent:%s:%s:%s", ctx.getProfile().getNamespace(), ctx.getFlow().getName(), ctx.getId());
        String key       = String.format("%s:%s", namespace, ctx.getAction().code());
        statusManagerMap.get(ctx.getProfile().getStatus()).idempotent(key);
    }


    public void unidempotent(C ctx) throws IdempotentException {
        String namespace = String.format("idempotent:%s:%s:%s", ctx.getProfile().getNamespace(), ctx.getFlow().getName(), ctx.getId());
        String key       = String.format("%s:%s", namespace, ctx.getAction().code());
        statusManagerMap.get(ctx.getProfile().getStatus()).unidempotent(key);
    }


    public void metricsNode(C ctx) {
        String            flowName = ctx.getFlow().getName();
        Serializable      id       = ctx.getId();
        State             state    = ctx.getState();
        StatisticsSupport ss       = statisticsSupportMap.get(ctx.getProfile().getStatistics());
        ss.increment(flowName, id, state, Coast.STATISTICS.EXECUTE_TIMES);
    }

    public Long getExecuteTimes(C ctx, State state) {
        String            flowName = ctx.getFlow().getName();
        Serializable      id       = ctx.getId();
        StatisticsSupport ss       = statisticsSupportMap.get(ctx.getProfile().getStatistics());
        return ss.get(flowName, id, state, Coast.STATISTICS.EXECUTE_TIMES);
    }

    public static <T> T getAction(Class<T> action) {
        Assert.notNull(action, "action is null");
        String runMode = System.getProperty(Coast.RUN_MODE);
        if (Objects.equals(runMode, Coast.RUN_MODE_CHAOS)) {
            if (LocalFsmAction.class.isAssignableFrom(action) || LocalSagaAction.class.isAssignableFrom(action)) {
                return SpringUtil.getBean(Coast.LOCAL_CHAOS_ACTION);
            } else {
                return SpringUtil.getBean(Coast.REMOTE_CHAOS_ACTION);
            }
        } else {
            return SpringUtil.getBean(action);
        }
    }

}


