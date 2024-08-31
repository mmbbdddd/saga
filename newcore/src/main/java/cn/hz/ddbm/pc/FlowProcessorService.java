package cn.hz.ddbm.pc;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.newcore.*;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.fsm.FsmActionProxy;
import cn.hz.ddbm.pc.newcore.fsm.FsmCommandAction;
import cn.hz.ddbm.pc.newcore.infra.*;
import cn.hz.ddbm.pc.newcore.infra.impl.*;
import cn.hz.ddbm.pc.newcore.infra.proxy.*;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaActionProxy;
import cn.hz.ddbm.pc.newcore.test.NoneFsmAction;
import cn.hz.ddbm.pc.newcore.test.NoneSagaAction;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FlowProcessorService<C extends FlowContext> implements FlowProcessor<C> {
    Map<String, FlowModel>                       flows;
    Map<Coast.SessionType, SessionManager>       sessionManagerMap;
    Map<Coast.StatusType, StatusManager>         statusManagerMap;
    Map<Coast.LockType, Locker>                  lockerMap;
    Map<Coast.ScheduleType, ScheduleManger>      scheduleMangerMap;
    Map<Coast.StatisticsType, StatisticsSupport> statisticsSupportMap;
    Map<String, Action>                          actionMap;

    PluginService pluginService;

    public FlowProcessorService() {
        this.flows                = new HashMap<>();
        this.sessionManagerMap    = new HashMap<>();
        this.statusManagerMap     = new HashMap<>();
        this.lockerMap            = new HashMap<>();
        this.scheduleMangerMap    = new HashMap<>();
        this.statisticsSupportMap = new HashMap<>();
        this.actionMap            = new HashMap<>();

        this.pluginService = new PluginService(getDefaultPlugins());
        this.statisticsSupportMap.put(Coast.StatisticsType.jvm, new JvmStatisticsSupport());
        this.sessionManagerMap.put(Coast.SessionType.jvm, new JvmSessionManager());
        this.statusManagerMap.put(Coast.StatusType.jvm, new JvmStatusManager());
        this.scheduleMangerMap.put(Coast.ScheduleType.timer, new TimerScheduleManager());
        this.lockerMap.put(Coast.LockType.jvm, new JvmLocker());
        this.actionMap.put(Coast.NONE_FSM_ACTION, new FsmActionProxy<>(new NoneFsmAction<>()));
        this.actionMap.put(Coast.NONE_SAGA_ACTION, new SagaActionProxy(new NoneSagaAction()));
    }

    @PostConstruct
    public void afterPropertiesSet() {
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
        SpringUtil.getBeansOfType(FlowFactory.class).forEach((key, flowFactory) -> {
            this.flows.putAll(flowFactory.getFlows());
        });
    }

    protected abstract List<Plugin> getDefaultPlugins();

    public FlowModel getFlow(String flowName) {
        return flows.get(flowName);
    }

    public PluginService plugin() {
        return pluginService;
    }

    public Map<String, Object> getSession(String flowName, Serializable id) throws SessionException {
        return sessionManagerMap.get(flowName).get(flowName, id);
    }

    public boolean tryLock(FlowContext ctx) {
        Profile profile = ctx.getProfile();
        String  key     = String.format("lock:%s:%s:%s", profile.getNamespace(), ctx.getFlow().getName(), ctx.getId());

        try {
            lockerMap.get(profile.getLock()).tryLock(key, profile.getLockTimeoutMicros());
            return true;
        } catch (LockException e) {
            return false;
        }
    }

    public void unLock(FlowContext ctx) {
        Profile profile = ctx.getProfile();
        String  key     = String.format("lock:%s:%s:%s", profile.getNamespace(), ctx.getFlow().getName(), ctx.getId());
        try {
            lockerMap.get(profile.getLock()).releaseLock(key);
        } catch (LockException e) {
            Logs.error.error("", e);
        }
    }

    public void updateStatus(FlowContext ctx) throws StatusException {
        ctx.syncpayload();
        statusManagerMap.get(ctx.getProfile().getStatus()).flush(ctx);
    }

    /**
     * 刷新状态到基础设施
     */
    public void flush(FlowContext ctx) throws SessionException, StatusException {
        ctx.syncpayload();
        sessionManagerMap.get(ctx.getProfile().getSession()).flush(ctx);
        statusManagerMap.get(ctx.getProfile().getStatus()).flush(ctx);
    }

    public void idempotent(String state, String event, FlowContext ctx) throws IdempotentException {
        String namespace  = String.format("transition:%s:%s:%s", ctx.getProfile().getNamespace(), ctx.getFlow().getName(), ctx.getId());
        String transition = String.format("do(%s,%s)", state, event);
        String key        = String.format("%s:%s", namespace, transition);
        statusManagerMap.get(ctx.getProfile().getStatus()).idempotent(key);
    }


    public void unidempotent(String state, String event, FlowContext ctx) throws IdempotentException {
        String namespace  = String.format("transition:%s:%s:%s", ctx.getProfile().getNamespace(), ctx.getFlow().getName(), ctx.getId());
        String transition = String.format("do(%s,%s)", state, event);
        String key        = String.format("%s:%s", namespace, transition);
        statusManagerMap.get(ctx.getProfile().getStatus()).unidempotent(key);
    }


    public boolean isRetryable(Throwable e, FlowContext ctx) {
        if (ReflectUtil.getFieldValue(e, "raw") != null && ReflectUtil.getFieldValue(e, "raw") instanceof Exception) {
            e = (Exception) ReflectUtil.getFieldValue(e, "raw");
        }
        Boolean isRetryException = false;
        isRetryException |= e instanceof IOException;
        return isRetryException;
    }

    public boolean isInterrupted(Throwable e, FlowContext ctx) {
        if (ReflectUtil.getFieldValue(e, "raw") != null && ReflectUtil.getFieldValue(e, "raw") instanceof Exception) {
            e = (Exception) ReflectUtil.getFieldValue(e, "raw");
        }
        Boolean isInterruptedException = false;
        isInterruptedException |= e instanceof LockException;
        return isInterruptedException;
    }

    public boolean isPaused(Throwable e, FlowContext ctx) {
        if (ReflectUtil.getFieldValue(e, "raw") != null && ReflectUtil.getFieldValue(e, "raw") instanceof Exception) {
            e = (Exception) ReflectUtil.getFieldValue(e, "raw");
        }
        Boolean isPausedException = false;
        isPausedException |= e instanceof IllegalArgumentException;
        return isPausedException;
    }

    public boolean isStoped(Throwable e, FlowContext ctx) {
        return e instanceof FlowEndException || FlowStatus.isEnd(ctx.getStatus());
    }


    public void metricsNode(FlowContext ctx) {
        String            flowName = ctx.getFlow().getName();
        Serializable      id       = ctx.getId();
        State             state    = ctx.getState();
        StatisticsSupport ss       = statisticsSupportMap.get(ctx.getProfile().getStatistics());
        ss.increment(flowName, id, state, Coast.STATISTICS.EXECUTE_TIMES);
    }

    public Long getExecuteTimes(FlowContext ctx, State state) {
        String            flowName = ctx.getFlow().getName();
        Serializable      id       = ctx.getId();
        StatisticsSupport ss       = statisticsSupportMap.get(ctx.getProfile().getStatistics());
        return ss.get(flowName, id, state, Coast.STATISTICS.EXECUTE_TIMES);
    }

    public <T> T getAction(String action, Class<T> type) {
        T actionBean = (T) actionMap.get(action);
        if (null == actionBean) {
            if (type.equals(SagaAction.class)) {
                return (T) actionMap.get(Coast.NONE_SAGA_ACTION);
            } else {
                return (T) actionMap.get(Coast.NONE_FSM_ACTION);
            }
        } else {
            return actionBean;
        }
    }
}


