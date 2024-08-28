package cn.hz.ddbm.pc.profile;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.core.Fsm;
import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.FsmPayload;
import cn.hz.ddbm.pc.core.Profile;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.enums.FlowStatus;
import cn.hz.ddbm.pc.core.exception.SessionException;
import cn.hz.ddbm.pc.core.exception.wrap.StatusException;
import cn.hz.ddbm.pc.core.log.Logs;
import cn.hz.ddbm.pc.core.utils.InfraUtils;
import cn.hz.ddbm.pc.profile.chaos.ChaosRule;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ChaosSagaService extends BaseService {

    ExecutorService      threadPool = Executors.newFixedThreadPool(20);
    List<StatisticsLine> statisticsLines;
    List<ChaosRule>      chaosRules;

    public ChaosSagaService() {
        this.chaosRules = new ArrayList<>();
    }

    public <S extends Enum<S>> void execute(String flowName, MockPayLoad<S> payload, String event, Integer times, Integer timeout, List<ChaosRule> rules, Boolean mock) {
        Assert.notNull(flowName, "flowName is null");
        Assert.notNull(payload, "FlowPayload is null");
        CountDownLatch cdl = new CountDownLatch(times);
        this.chaosRules = rules;
        statisticsLines = Collections.synchronizedList(new ArrayList<>(times));
        for (int i = 0; i < times; i++) {
            MockPayLoad<S> mockPayLoad = payload.copy(i);
            mockPayLoad.setId(i);
            threadPool.submit(() -> {
                try {
                    FsmContext<S, MockPayLoad<S>> ctx = standalone(flowName, mockPayLoad, event, mock);
                    statistics(mockPayLoad.getId(), new Object[]{flowName, mockPayLoad, event}, ctx);
                } catch (Throwable t) {
                    Logs.error.error("", t);
                    statistics(mockPayLoad.getId(), new Object[]{flowName, mockPayLoad, event}, t);
                } finally {
                    cdl.countDown();
                }
            });
        }
        try {
            cdl.await(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Logs.error.error("", e);
            throw new RuntimeException(e);
        }
        printStatisticsReport();
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

    private void statistics(Serializable i, Object requestInfo, FsmContext result) {
        statisticsLines.add(new StatisticsLine(i, requestInfo, result));
    }

    private void statistics(Serializable i, Object requestInfo, Throwable e) {
        statisticsLines.add(new StatisticsLine(i, requestInfo, e));
    }

    private <S extends Enum<S>> FsmContext<S, MockPayLoad<S>> standalone(String flowName, MockPayLoad<S> payload, String event, Boolean mock) throws StatusException, SessionException {
        event = StrUtil.isBlank(event) ? Coasts.EVENT_DEFAULT : event;
        Fsm                           flow = getFlow(flowName);
        FsmContext<S, MockPayLoad<S>> ctx  = new FsmContext<S, MockPayLoad<S>>(flow, payload, event, Profile.chaosOf());
        ctx.setMockBean(mock);
        ctx.setIsChaos(true);
        while (chaosIsContine(ctx)) {
            execute(ctx);
        }
        return ctx;
    }

    public <S extends Enum<S>> boolean chaosIsContine(FsmContext<S, MockPayLoad<S>> ctx) {

        String flowName = ctx.getFlow().getName();
        S      state    = ctx.getState();
        if (!ctx.getFlow().isRunnable(state)) {
            Logs.flow.debug("流程不可运行：{},{},{},{}", flowName, ctx.getId(), ctx.getStatus(), ctx.getState());
            return false;
        }

        Long    executeCount = InfraUtils.getMetricsTemplate().get(ctx.getFlow().getName(), ctx.getId(), state, Coasts.EXECUTE_COUNT);
        Integer nodeRetry    = ctx.getFlow().getNode(state).getRetry();

        if (executeCount > nodeRetry) {
            Logs.flow.warn("流程已限流：{},{},{},{}>{}", flowName, ctx.getId(), state, executeCount, nodeRetry);
            return false;
        }
        return true;
    }


    public List<ChaosRule> chaosRules() {
        return chaosRules;
    }


    public static class MockPayLoad<S extends Enum<S>> implements FsmPayload<S> {
        Integer    id;
        FlowStatus status;
        S          state;

        public MockPayLoad(S init) {
            this.status = FlowStatus.INIT;
            this.state  = init;
        }

        @Override
        public Serializable getId() {
            return id;
        }

        public void setId(int i) {
            this.id = i;
        }

        @Override
        public FlowStatus getStatus() {
            return status;
        }

        @Override
        public S getState() {
            return state;
        }

        @Override
        public void setStatusSate(FlowStatus status, S state) {

        }


        public MockPayLoad<S> copy(Integer id) {
            MockPayLoad<S> copy = new MockPayLoad<>(this.state);
            copy.setId(id);
            return copy;
        }
    }

    static class StatisticsLine {
        Serializable index;
        Object       requestInfo;
        TypeValue    result;

        public StatisticsLine(Serializable i, Object o, FsmContext result) {
            this.index       = i;
            this.requestInfo = o;
            this.result      = new TypeValue(result);
        }

        public StatisticsLine(Serializable i, Object o, Throwable e) {
            this.index       = i;
            this.requestInfo = o;
            this.result      = new TypeValue(e);
        }
    }

    static class TypeValue {
        String type;
        String value;

        public TypeValue(Throwable t) {
            this.type  = t.getClass().getSimpleName();
            this.value = t.getMessage();
        }

        public TypeValue(FsmContext<?, ?> ctx) {
            this.type  = ctx.getClass().getSimpleName();
            this.value = String.format("%s:%s", ctx.getState(), ctx.getStatus());
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

}
