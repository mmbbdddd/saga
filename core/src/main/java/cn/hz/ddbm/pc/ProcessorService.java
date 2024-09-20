package cn.hz.ddbm.pc;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.newcore.*;
import cn.hz.ddbm.pc.newcore.chaos.LocalChaosAction;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.SessionException;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.fsm.actions.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.infra.*;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmLocker;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmSessionManager;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmStatisticsSupport;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmStatusManager;
import cn.hz.ddbm.pc.newcore.infra.proxy.*;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.saga.actions.LocalSagaAction;
import cn.hz.ddbm.pc.newcore.utils.EnvUtils;

import javax.swing.text.LayoutQueue;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ProcessorService {
    Map<String, BaseFlow>                        flows;
    Map<Coast.SessionType, SessionManager>       sessionManagerMap;
    Map<Coast.StatusType, StatusManager>         statusManagerMap;
    Map<Coast.LockType, Locker>                  lockerMap;
    Map<Coast.ScheduleType, ScheduleManger>      scheduleMangerMap;
    Map<Coast.StatisticsType, StatisticsSupport> statisticsSupportMap;
    PluginService                                pluginService;

    public ProcessorService() {
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

    /**
     * 连续执行
     *
     * @param ctx
     * @throws ActionException
     */
    public void execute(FlowContext ctx) throws ActionException {
        BaseFlow flow = ctx.getFlow();
        while (flow.keepRun(ctx)) {
            try {
                flow.execute(ctx);
            } catch (RuntimeException e) {
                //运行时异常中断
                flushState(ctx);
                flushSession(ctx);
                throw e;
            } catch (Exception e) {
                flushState(ctx);
                flushSession(ctx);
//                其他尝试重试
                addRetryTask(ctx);
            }
        }
    }

    private void flushState(FlowContext ctx) {
        //todo

    }

    private void flushSession(FlowContext ctx) {
        //todo
    }

    private void addRetryTask(FlowContext ctx) {
        //todo
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


    public Map<String, Object> getSession(String flowName, Serializable id) throws SessionException {
        BaseFlow flow    = getFlow(flowName);
        Profile  profile = flow.getProfile();
        return sessionManagerMap.get(profile.getSession()).get(flowName, id);
    }


    public void metricsNode(FlowContext ctx) {
        String            flowName = ctx.getFlow().getName();
        Serializable      id       = ctx.getId();
        State             state    = ctx.getState();
        StatisticsSupport ss       = statisticsSupportMap.get(ctx.getProfile().getStatistics());
        ss.increment(flowName, id, state, Coast.STATISTICS.EXECUTE_TIMES);
    }

    public Long getExecuteTimes(FlowContext ctx) {
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
                return (T) SpringUtil.getBean(LocalChaosAction.class);
            } else {
                return SpringUtil.getBean(Coast.REMOTE_CHAOS_ACTION);
            }
        } else {
            return SpringUtil.getBean(action);
        }
    }

    public FlowContext<SagaState> getSagaContext(String flowName, Object o) {
        return null;
    }
    public FlowContext<SagaState> getFsmContext(String flowName, Object o) {
        return null;
    }
}


