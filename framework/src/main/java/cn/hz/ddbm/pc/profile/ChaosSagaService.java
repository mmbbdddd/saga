//package cn.hz.ddbm.pc.profile;
//
//import cn.hutool.core.lang.Assert;
//import cn.hutool.core.lang.Pair;
//import cn.hutool.core.util.StrUtil;
//import cn.hz.ddbm.pc.common.lang.Triple;
//import cn.hz.ddbm.pc.core.*;
//import cn.hz.ddbm.pc.core.coast.Coasts;
//import cn.hz.ddbm.pc.core.enums.FlowStatus;
//import cn.hz.ddbm.pc.core.exception.SessionException;
//import cn.hz.ddbm.pc.core.exception.StatusException;
//import cn.hz.ddbm.pc.core.log.Logs;
//import cn.hz.ddbm.pc.core.processor.saga.SagaState;
//import cn.hz.ddbm.pc.core.utils.InfraUtils;
//import cn.hz.ddbm.pc.profile.chaos.ChaosRule;
//
//import java.io.Serializable;
//import java.util.*;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//public class ChaosSagaService extends BaseService {
//
//    ExecutorService      threadPool = Executors.newFixedThreadPool(20);
//    List<StatisticsLine> statisticsLines;
//    List<ChaosRule>      chaosRules;
//
//    public ChaosSagaService() {
//        this.chaosRules = new ArrayList<>();
//    }
//
//    public void execute(String flowName, MockPayLoad payload, String event, Integer times, Integer timeout, List<ChaosRule> rules, Boolean mock) {
//        Assert.notNull(flowName, "flowName is null");
//        Assert.notNull(payload, "FlowPayload is null");
//        CountDownLatch cdl = new CountDownLatch(times);
//        this.chaosRules = rules;
//        statisticsLines = Collections.synchronizedList(new ArrayList<>(times));
//        for (int i = 0; i < times; i++) {
//            MockPayLoad  mockPayLoad = payload.copy(i);
//            mockPayLoad.setId(i);
//            threadPool.submit(() -> {
//                try {
//                    FsmContext ctx = standalone(flowName, mockPayLoad, event, mock);
//                    statistics(mockPayLoad.getId(), new Object[]{flowName, mockPayLoad, event}, ctx);
//                } catch (Throwable t) {
//                    Logs.error.error("", t);
//                    statistics(mockPayLoad.getId(), new Object[]{flowName, mockPayLoad, event}, t);
//                } finally {
//                    cdl.countDown();
//                }
//            });
//        }
//        try {
//            cdl.await(timeout, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            Logs.error.error("", e);
//            throw new RuntimeException(e);
//        }
//        printStatisticsReport();
//    }
//
//    private void printStatisticsReport() {
//        Map<Pair<String, String>, List<StatisticsLine>> groups = statisticsLines.stream()
//                .collect(Collectors.groupingBy(t -> Pair.of(t.result.type, t.result.value)));
//        Logs.flow.info("混沌测试报告：\\n");
//        groups.forEach((triple, list) -> {
//            Logs.flow.info("{},{},{}", triple.getKey(), triple.getValue(), list.size());
//        });
//        statisticsLines.clear();
//    }
//
//    private void statistics(Serializable i, Object requestInfo, FsmContext result) {
//        statisticsLines.add(new StatisticsLine(i, requestInfo, result));
//    }
//
//    private void statistics(Serializable i, Object requestInfo, Throwable e) {
//        statisticsLines.add(new StatisticsLine(i, requestInfo, e));
//    }
//
//    private FsmContext standalone(String flowName, MockPayLoad payload, String event, Boolean mock) throws StatusException, SessionException {
//        event = StrUtil.isBlank(event) ? Coasts.EVENT_FORWARD : event;
//        Fsm        flow = getFlow(flowName);
//        FsmContext ctx  = new FsmContext(flow, payload, event, Profile.chaosOf());
//        ctx.setMockBean(mock);
//        ctx.setIsChaos(true);
//        ctx.setFluent(true);
//        while (ctx.getFluent() && chaosIsContine(ctx)) {
//            execute(ctx);
//        }
//        return ctx;
//    }
//
//    public <S extends Enum<S>> boolean chaosIsContine(FsmContext ctx) {
//
//        String     flowName = ctx.getFlow().getName();
//        FlowStatus status   = ctx.getFlowStatus();
//        if (FlowStatus.isEnd(status)) {
//            Logs.flow.debug("流程不可运行：{},{},{},{}", flowName, ctx.getId(), ctx.getStatus(), ctx.getState());
//            return false;
//        }
//        State state = ctx.getState();
//        if (!ctx.getFlow().isRunnableState(state)) {
//            Logs.flow.debug("流程不可运行：{},{},{},{}", flowName, ctx.getId(), ctx.getStatus(), ctx.getState());
//            return false;
//        }
//
//        Long    executeCount = InfraUtils.getMetricsTemplate().get(ctx.getFlow().getName(), ctx.getId(), state, Coasts.EXECUTE_COUNT);
//        Integer nodeRetry    = ctx.getProfile().getStateRetry(state);
//
//        if (executeCount > nodeRetry) {
//            Logs.flow.warn("流程已限流：{},{},{},{}>{}", flowName, ctx.getId(), state, executeCount, nodeRetry);
//            return false;
//        }
//        return true;
//    }
//
//
//    public List<ChaosRule> chaosRules() {
//        return chaosRules;
//    }
//
//
//    public static class MockPayLoad<S extends State> implements FsmPayload<S> {
//        Integer    id;
//        FlowStatus status;
//        S      state;
//        String     event;
//
//        public MockPayLoad(S init) {
//            this.status = FlowStatus.INIT;
//            this.state  = init;
//            this.event  = Coasts.EVENT_DEFAULT;
//        }
//
//
//        public MockPayLoad<S> copy(Integer id) {
//            MockPayLoad<S> copy = new MockPayLoad<>(this.state);
//            copy.setId(id);
//            return copy;
//        }
//
//        @Override
//        public Serializable getId() {
//            return id;
//        }
//
//        @Override
//        public Triple<FlowStatus, S, String> getStatus() {
//            return Triple.of(status,state,event);
//        }
//
//        @Override
//        public void setStatus(Triple<FlowStatus, S, String> status) {
//            //todo
//        }
//
//
////        @Override
////        public void setStatus(Triple status) {
////
////        }
////
////        @Override
////        public void setStatus(Triple<FlowStatus, State, String> triple) {
////            this.state  = triple.getMiddle();
////            this.status = triple.getLeft();
////            this.event  = triple.getRight();
////        }
//
//        public void setId(int i) {
//            this.id = i;
//        }
//    }
//
//    static class StatisticsLine {
//        Serializable index;
//        Object       requestInfo;
//        TypeValue    result;
//
//        public StatisticsLine(Serializable i, Object o, FsmContext result) {
//            this.index       = i;
//            this.requestInfo = o;
//            this.result      = new TypeValue(result);
//        }
//
//        public StatisticsLine(Serializable i, Object o, Throwable e) {
//            this.index       = i;
//            this.requestInfo = o;
//            this.result      = new TypeValue(e);
//        }
//    }
//
//    static class TypeValue {
//        String type;
//        String value;
//
//        public TypeValue(Throwable t) {
//            this.type  = t.getClass().getSimpleName();
//            this.value = t.getMessage();
//        }
//
//        public TypeValue(FsmContext  ctx) {
//            this.type  = ctx.getClass().getSimpleName();
//            this.value = String.format("%s:%s", ctx.getState(), ctx.getStatus());
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            TypeValue typeValue = (TypeValue) o;
//            return Objects.equals(type, typeValue.type) && Objects.equals(value, typeValue.value);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(type, value);
//        }
//
//        @Override
//        public String toString() {
//            return "TypeValue{" +
//                    "type='" + type + '\'' +
//                    ", value='" + value + '\'' +
//                    '}';
//        }
//    }
//
//}
