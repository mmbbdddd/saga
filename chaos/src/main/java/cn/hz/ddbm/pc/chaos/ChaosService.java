package cn.hz.ddbm.pc.chaos;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.chaos.support.ChaosAction;
import cn.hz.ddbm.pc.chaos.support.ChaosHandlerImpl;
import cn.hz.ddbm.pc.chaos.support.ChaosRule;
import cn.hz.ddbm.pc.factory.fsm.BeanFsmFlowFactory;
import cn.hz.ddbm.pc.factory.saga.BeanSagaFlowFactory;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.FlowEndException;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.PauseException;
import cn.hz.ddbm.pc.newcore.exception.SessionException;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmPayload;
import cn.hz.ddbm.pc.newcore.fsm.FsmProcessor;
import cn.hz.ddbm.pc.newcore.infra.InfraUtils;
import cn.hz.ddbm.pc.newcore.infra.StatisticsSupport;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmLocker;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmSessionManager;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmStatisticsSupport;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmStatusManager;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.SagaPayload;
import cn.hz.ddbm.pc.newcore.saga.SagaProcessor;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.support.BaseService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class ChaosService extends BaseService {
    ExecutorService      threadPool = Executors.newFixedThreadPool(20);
    List<StatisticsLine> statisticsLines;
    @Autowired
    ChaosHandlerImpl chaosHandler;


    public void executeSAGAs(String flowName, Enum initStatus,  Integer times, Integer timeout,Boolean mockBean, List<ChaosRule> rules) throws PauseException, SessionException, FlowEndException, InterruptedException {
        System.setProperty(Coast.RUN_MODE, Coast.RUN_MODE_CHAOS);
        chaosHandler.setChaosRules(rules);
        statisticsLines = Collections.synchronizedList(new ArrayList<>(times));
        CountDownLatch cdl = new CountDownLatch(times);
        for (int i = 0; i < times; i++) {
            MockSagaPayload mockPayLoad = new MockSagaPayload(i, initStatus);
            mockPayLoad.setId(i);
            threadPool.submit(() -> {
                Object result = null;
                try {
                    SagaContext ctx = sagaProcessor.getContext(flowName, mockPayLoad);
                    while (chaosIsContine(ctx)) {
                        sagaProcessor.flowProcess(ctx);
                    }
                    result = ctx;
                } catch (Throwable t) {
                    Logs.error.error("", t);
                    result = t;
                } finally {
                    cdl.countDown();
//                    统计执行结果
                    statistics(mockPayLoad.getId(), new Object[]{flowName, mockPayLoad}, result);
                }
            });
        }
        try {
            cdl.await(timeout, TimeUnit.SECONDS);
        } catch (java.lang.InterruptedException e) {
            Logs.error.error("", e);
            throw new RuntimeException(e);
        }
        printStatisticsReport();
    }


    public void executeFSMs(String flowName, Enum initStatus,  Integer times, Integer timeout,Boolean mockBean, List<ChaosRule> rules) throws PauseException, SessionException, FlowEndException, InterruptedException {
        System.setProperty(Coast.RUN_MODE, Coast.RUN_MODE_CHAOS);
        chaosHandler.setChaosRules(rules);
        statisticsLines = Collections.synchronizedList(new ArrayList<>(times));
        CountDownLatch cdl = new CountDownLatch(times);
        for (int i = 0; i < times; i++) {
            MockFsmPayload mockPayLoad = new MockFsmPayload(i, initStatus);
            mockPayLoad.setId(i);
            threadPool.submit(() -> {
                Object result = null;
                try {
                    FsmContext ctx = fsmProcessor.getContext(flowName, mockPayLoad);
                    while (chaosIsContine(ctx)) {
                        fsmProcessor.flowProcess(ctx);
                    }
                    result = ctx;
                } catch (Throwable t) {
                    Logs.error.error("", t);
                    result = t;
                } finally {
                    cdl.countDown();
//                    统计执行结果
                    statistics(mockPayLoad.getId(), new Object[]{flowName, mockPayLoad}, result);
                }
            });
        }
        try {
            cdl.await(timeout, TimeUnit.SECONDS);
        } catch (java.lang.InterruptedException e) {
            Logs.error.error("", e);
            throw new RuntimeException(e);
        }
        printStatisticsReport();
    }

    public boolean chaosIsContine(FlowContext ctx) {

        String     flowName = ctx.getFlow().getName();
        State      state    = ctx.getState();
        FlowStatus status   = ctx.getStatus();
        if (!FlowStatus.isRunnable(status)) {
            Logs.flow.debug("流程不可运行：{},{},{},{}", flowName, ctx.getId(), status, state);
            return false;
        }

        Long    executeCount = fsmProcessor.getExecuteTimes(ctx, ctx.getState());
        Integer nodeRetry    = ctx.getFlow().getRetry(ctx.getState());

        if (executeCount > nodeRetry) {
            Logs.flow.warn("流程已限流：{},{},{},{}>{}", flowName, ctx.getId(), state, executeCount, nodeRetry);
            return false;
        }
        return true;
    }

    private void printStatisticsReport() {
        Map<Pair<String, String>, List<StatisticsLine>> groups = statisticsLines.stream()
                .collect(Collectors.groupingBy(t -> Pair.of(t.result.type, t.result.value)));
        Logs.flow.info("混沌测试报告：\\n");
        groups.forEach((triple, list) -> {
            Logs.flow.info("{},{},{}", triple.getKey(), triple.getValue(), list.size());
        });

        statisticsLines.clear();
    }

    private void statistics(Serializable i, Object requestInfo, Object result) {
        statisticsLines.add(new StatisticsLine(i, requestInfo, result));
    }

}


class MockFsmPayload<S extends Enum<S>> extends FsmPayload<S> {

    public MockFsmPayload(Serializable id, S fsmState) {
        super(id, FlowStatus.INIT, fsmState);
    }
}

@Data
class MockSagaPayload<S extends Enum<S>> implements SagaPayload<S> {

    Integer          id;
    FlowStatus       status;
    S                sagaState;
    SagaState.Offset offset;
    Boolean          forward;

    public MockSagaPayload(Integer id, S sagaState) {
        this.id        = id;
        this.sagaState = sagaState;
        this.forward   = true;
        this.offset    = SagaState.Offset.task;
        this.status    = FlowStatus.INIT;
    }
}

class StatisticsLine {
    Serializable index;
    Object       requestInfo;
    TypeValue    result;

    public StatisticsLine(Serializable i, Object o, Object result) {
        this.index       = i;
        this.requestInfo = o;
        if (result instanceof Throwable) {
            this.result = new TypeValue((Throwable) result);
        } else if (result instanceof FlowContext) {
            this.result = new TypeValue((FlowContext<?, ?, ?>) result);
        } else {
            this.result = null;
        }
    }
}

class TypeValue {
    String type;
    String value;

    public TypeValue(Throwable t) {
        this.type  = t.getClass().getSimpleName();
        this.value = t.getMessage();
    }

    public TypeValue(FlowContext<?, ?, ?> ctx) {
        this.type  = ctx.getClass().getSimpleName();
        this.value = ctx.getState().code().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeValue typeValue = (TypeValue) o;
        return Objects.equals(type, typeValue.type) && Objects.equals(value, typeValue.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        return "TypeValue{" +
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}



