package cn.hz.ddbm.pc.newcore.chaos;

import cn.hz.ddbm.pc.ProcessorService;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.BaseFlow;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.FlowEndException;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.PauseException;
import cn.hz.ddbm.pc.newcore.exception.SessionException;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.saga.SagaFlow;
import cn.hz.ddbm.pc.newcore.saga.SagaProcessor;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.utils.EnvUtils;
import cn.hz.ddbm.pc.newcore.utils.ExceptionUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class ChaosService {
    ExecutorService      threadPool = Executors.newFixedThreadPool(20);
    List<StatisticsLine> statisticsLines;

    @Resource
    protected ProcessorService processorService;
    @Autowired
    ChaosHandler chaosHandler;


    public void saga(String flowName, Boolean mock, Integer retry, Integer times, Integer timeout, ChaosConfig chaosConfig) throws PauseException, SessionException, FlowEndException, InterruptedException {
        EnvUtils.setChaosMode(mock);
        chaosHandler.setChaosConfig(chaosConfig);
        Coast.DEFAULT_RETRYTIME = retry;
        statisticsLines         = Collections.synchronizedList(new ArrayList<>(times));
        CountDownLatch cdl  = new CountDownLatch(times);
        BaseFlow       flow = (SagaFlow) processorService.getFlow(flowName);
        for (int i = 0; i < times; i++) {
//            MockSagaPayload mockPayLoad = new MockSagaPayload(i, (SagaFlow) flow);
//            mockPayLoad.setId(i);
            threadPool.submit(() -> {
                Object result = null;
                try {
                    FlowContext<SagaState> ctx = processorService.getSagaContext(flowName, null);
                    while (isContinue(ctx)) {
                        Logs.debug.info("uuid：{}",ctx.getUuid());
                        processorService.execute(ctx);
                    }
                    result = ctx;
                } catch (Throwable t) {
                    Logs.error.error("", ExceptionUtils.unwrap(t));
                    result = t;
                } finally {
                    cdl.countDown();
//                    统计执行结果
//                    statistics(mockPayLoad.getId(), new Object[]{flowName, mockPayLoad}, result);
                    statistics(null, new Object[]{flowName, null}, result);
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


    public void fsm(String flowName, Boolean mock, Enum initStatus, Integer retry, Integer times, Integer timeout, ChaosConfig chaosConfig) throws PauseException, SessionException, FlowEndException, InterruptedException {
        EnvUtils.setChaosMode(mock);
        Coast.DEFAULT_RETRYTIME = retry;
        chaosHandler.setChaosConfig(chaosConfig);
        statisticsLines = Collections.synchronizedList(new ArrayList<>(times));
        CountDownLatch cdl = new CountDownLatch(times);
        for (int i = 0; i < times; i++) {
//            MockFsmPayload mockPayLoad = new MockFsmPayload(i, initStatus);
//            MockFsmPayload mockPayLoad = new MockFsmPayload(i, initStatus);
//            mockPayLoad.setId(i);
            threadPool.submit(() -> {
                Object result = null;
                try {
                    FlowContext ctx = processorService.getFsmContext(flowName, null);
                    while (isContinue(ctx)) {
                        processorService.execute(ctx);
                    }
                    result = ctx;
                } catch (Throwable t) {
                    Logs.error.error("", t);
                    result = t;
                } finally {
                    cdl.countDown();
//                    统计执行结果
//                    statistics(mockPayLoad.getId(), new Object[]{flowName, mockPayLoad}, result);
                    statistics(null, new Object[]{flowName, null}, result);
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

    public boolean isContinue(FlowContext ctx) {

        BaseFlow flow     = ctx.getFlow();
        String   flowName = flow.getName();
        State     state    = ctx.getState();
        if (!flow.keepRun(ctx)) {
            Logs.flow.debug("流程不可运行：{},{},{} ", flowName, ctx.getId(), state);
            return false;
        }

        Long executeCount = processorService.getExecuteTimes(ctx);
        Integer nodeRetry = ctx.getFlow() .getRetry(ctx.getState());

        if (executeCount > nodeRetry) {
            Logs.flow.warn("流程已限流：{},{},{},{}>{}", flowName, ctx.getId(), state, executeCount, nodeRetry);
            return false;
        }
        return true;
    }

    private void printStatisticsReport() {
        Map<String, List<StatisticsLine>> groups = statisticsLines.stream()
                .collect(Collectors.groupingBy(t -> t.result.value));
        Logs.flow.info("混沌测试报告：\\n");
        groups.forEach((key, list) -> {
            Logs.flow.info("{},\t{}", key, list.size());
        });

        statisticsLines.clear();
    }

    private void statistics(Serializable i, Object requestInfo, Object result) {
        statisticsLines.add(new StatisticsLine(i, requestInfo, result));
    }

}

//@Data
//class MockFsmPayload<S extends Enum<S>> implements FsmPayload<S> {
//    Serializable    id;
//    FlowStatus      status;
//    S               fsmState;
//    FsmState.Offset offset;
//
//    public MockFsmPayload(Serializable id, S fsmState) {
//        this.id       = id;
//        this.status   = FlowStatus.RUNNABLE;
//        this.fsmState = fsmState;
//        this.offset   = FsmState.Offset.task;
//    }
//
//    @Override
//    public FsmState<S> getState() {
//        return new FsmState<>(fsmState, offset);
//    }
//
//    @Override
//    public void setState(FsmState<S> state) {
//        this.fsmState = state.getState();
//        this.offset   = state.getOffset();
//        this.status   = state.getStatus();
//    }
//}

//@Data
//class MockSagaPayload<S extends Enum<S>> implements SagaPayload<S> {
//
//    Integer          id;
//    Integer          step;
//    SagaState.Offset offset;
//    FlowStatus       status;
//    SagaFlow<S>      flow;
//
//    public MockSagaPayload(Integer id, SagaFlow<S> flow) {
//        this.id     = id;
//        this.step   = 1;
//        this.offset = SagaState.Offset.task;
//        this.flow   = flow;
//    }
//
//    @Override
//    public SagaState<S> getState() {
//        return new SagaState<>(step, SagaState.Offset.task, status);
//    }
//
//    @Override
//    public void setState(SagaState<S> state) {
//        this.step   = state.getIndex();
//        this.offset = state.getOffset();
//        this.status = state.getStatus();
//    }
//
//
//}

class StatisticsLine {
    Serializable     index;
    Object           requestInfo;
    StatisticsResult result;

    public StatisticsLine(Serializable i, Object o, Object result) {
        this.index       = i;
        this.requestInfo = o;
        if (result instanceof Throwable) {
            this.result = new StatisticsResult((Throwable) result);
        } else if (result instanceof FlowContext) {
            this.result = new StatisticsResult((FlowContext) result);
        } else {
            this.result = null;
        }
    }
}

class StatisticsResult {
    Boolean isResult;
    String  value;

    public StatisticsResult(Throwable t) {
        this.isResult = false;
        this.value    = t.getClass()
                .getSimpleName() + ":" + t.getMessage();
    }

    public StatisticsResult(FlowContext  ctx) {
        this.isResult = true;
        this.value    = ctx.getState()
                .code()
                .toString();
    }

    @Override
    public String toString() {
        return "{" +
                "isResult=" + isResult +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        StatisticsResult that = (StatisticsResult) object;
        return Objects.equals(isResult, that.isResult) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isResult, value);
    }
}



