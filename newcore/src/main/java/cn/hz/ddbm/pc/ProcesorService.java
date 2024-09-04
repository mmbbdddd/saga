package cn.hz.ddbm.pc;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.newcore.*;
import cn.hz.ddbm.pc.newcore.chaos.ChaosHandler;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.infra.*;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmLocker;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmSessionManager;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmStatisticsSupport;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmStatusManager;
import cn.hz.ddbm.pc.newcore.infra.proxy.*;
import cn.hz.ddbm.pc.newcore.log.Logs;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class ProcesorService<C extends FlowContext> implements FlowProcessor<C>, InitializingBean {
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

    public static ChaosHandler chaosHandler() {
        return SpringUtil.getBean(ChaosHandler.class);
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


    protected abstract List<Plugin> getDefaultPlugins();

    public FlowModel getFlow(String flowName) {
        return flows.get(flowName);
    }

    public PluginService plugin() {
        return pluginService;
    }

    public Map<String, Object> getSession(String flowName, Serializable id) throws SessionException {
        FlowModel flow    = getFlow(flowName);
        Profile   profile = flow.getProfile();
        return sessionManagerMap.get(profile.getSession()).get(flowName, id);
    }

    public boolean tryLock(FlowContext ctx) throws LockException {
        Profile profile = ctx.getProfile();
        String  key     = String.format("lock:%s:%s:%s", profile.getNamespace(), ctx.getFlow().getName(), ctx.getId());

        try {
            lockerMap.get(profile.getLock()).tryLock(key, profile.getLockTimeoutMicros());
            return true;
        } catch (LockException e) {
            throw new LockException(e);
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

    public void idempotent(String action, FlowContext ctx) throws IdempotentException {
        String namespace = String.format("idempotent:%s:%s:%s", ctx.getProfile().getNamespace(), ctx.getFlow().getName(), ctx.getId());
        String key       = String.format("%s:%s", namespace, action);
        statusManagerMap.get(ctx.getProfile().getStatus()).idempotent(key);
    }


    public void unidempotent(String action, FlowContext ctx) throws IdempotentException {
        String namespace = String.format("idempotent:%s:%s:%s", ctx.getProfile().getNamespace(), ctx.getFlow().getName(), ctx.getId());
        String key       = String.format("%s:%s", namespace, action);
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

    public static <T> T getAction(Class<T> action) {
        Assert.notNull(action, "action is null");
        String runMode = System.getProperty(Coast.RUN_MODE);
        if (Objects.equals(runMode, Coast.RUN_MODE_CHAOS)) {
            return SpringUtil.getBean(Coast.CHAOS_ACTION);
        } else {
            return SpringUtil.getBean(action);
        }
    }

}


