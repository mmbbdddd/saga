package cn.hz.ddbm.pc.core.processor.saga;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.core.*;
import cn.hz.ddbm.pc.core.action.NoSuchRecordException;
import cn.hz.ddbm.pc.core.action.SagaAction;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.enums.FlowStatus;
import cn.hz.ddbm.pc.core.exception.IdempotentException;
import cn.hz.ddbm.pc.core.exception.StatusException;
import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class SagaFsmHandler implements TransitionHandler {
    ForwardQuantum forward;
    BackoffQuantum backoff;
    String         sagaAction;

    public SagaFsmHandler(State state, State failover, State su, State rollback, State rollbackFailover, State pre, String sagaAction) {
        Assert.notNull(state, "state iState null");
        Assert.notNull(failover, "failover iState null");
        Assert.notNull(su, "su iState null");
        Assert.notNull(rollback, "rollback iState null");
        Assert.notNull(rollbackFailover, "rollbackFailover iState null");
        this.forward    = new ForwardQuantum(state, failover, su, rollback, sagaAction);
        this.backoff    = new BackoffQuantum(rollback, rollbackFailover, pre, sagaAction);
        this.sagaAction = sagaAction;
    }


    public void onEvent(SagaProcessor processor, FsmContext ctx) throws StatusException, IdempotentException {
        if (Objects.equals(Coasts.EVENT_FORWARD, ctx.getEvent())) {
            forward.onEvent(processor, ctx);
        } else {
            backoff.onEvent(processor, ctx);
        }
    }

    public Set<Transition> toTransitions() {
        Set<Transition> set = new HashSet<>();
        set.add(new Transition(ProcessorType.SAGA, forward.getTask(), Coasts.EVENT_FORWARD, sagaAction, this));
        set.add(new Transition(ProcessorType.SAGA, forward.getFailover(), Coasts.EVENT_FORWARD, sagaAction, this));
        set.add(new Transition(ProcessorType.SAGA, backoff.getRollback(), Coasts.EVENT_BACKOFF, sagaAction, this));
        set.add(new Transition(ProcessorType.SAGA, backoff.getRollbackFailover(), Coasts.EVENT_BACKOFF, sagaAction, this));
        return set;
    }

    @Data
    public static class ForwardQuantum {
        State  task;
        State  failover;
        State  fail;
        State  su;
        String sagaActionName;

        public ForwardQuantum(State task, State failover, State su, State fail, String sagaActionName) {
            Assert.notNull(task, "task iState null");
            Assert.notNull(failover, "failover iState null");
            Assert.notNull(fail, "fail iState null");
            Assert.notNull(su, "su iState null");
            this.task           = task;
            this.failover       = failover;
            this.fail           = fail;
            this.su             = su;
            this.sagaActionName = sagaActionName;
        }

        public void onEvent(SagaProcessor processor, FsmContext ctx) throws StatusException, IdempotentException {
            State      state      = ctx.getState();
            SagaAction sagaAction = ctx.getAction();
            //如果任务可执行
            if (Objects.equals(state, task)) {
                //加锁
                if (!processor.tryLock(ctx)) {
                    return;
                }
                //设置容错
                processor.setFailover(ctx, failover);
                //冥等
                processor.idempotent(ctx.getFlow().getName(), ctx.getId(), state, ctx.getEvent());
                //执行业务
                try {
                    sagaAction.exec(ctx);
                } catch (Exception e) {
                    ctx.setState(failover);
                } finally {
                    processor.unLock(ctx);
                    processor.onActionFinallyPlugin(ctx);
                    ctx.metricsNode(ctx);
                }
            } else if (Objects.equals(state, failover)) {
                try {
                    Boolean queryResult = sagaAction.executeQuery(ctx);
                    //如果业务未发送成功，取消冥等
                    if (Objects.equals(queryResult, null)) {
                        processor.unidempotent(ctx.getFlow().getName(), ctx.getId(), state, ctx.getEvent());
                        ctx.setState(task);
                    }
                    if (!queryResult) {
                        processor.unidempotent(ctx.getFlow().getName(), ctx.getId(), state, ctx.getEvent());
                        //失败补偿策略:反复执行。直接失败
                        Integer retryTimes       = ctx.getProfile().getStateAttrs().get(task).getRetry();
                        Integer executeTimeState = processor.executeTimes(ctx, task);
                        if (executeTimeState > retryTimes) {
                            //失败处理机制：前向转后向，后向转人工
                            ctx.setEvent(Coasts.EVENT_BACKOFF);
                        } else {
                            //如果可以重试，则设置为初始状态，重新执行任务。
                            ctx.setState(task);
                        }
                    }
                    if (queryResult) {
                        ctx.setState(su);
                    }
                } catch (NoSuchRecordException e) {
                    processor.unidempotent(ctx.getFlow().getName(), ctx.getId(), state, ctx.getEvent());
                    ctx.setState(task);
                } catch (Exception e) {
                    ctx.setState(failover);
                } finally {
                    processor.onActionFinallyPlugin(ctx);
                    ctx.metricsNode(ctx);
                }
            }
        }

    }

    @Data
    public static class BackoffQuantum {
        State  rollback;
        State  rollbackFailover;
        State  pre;
        String sagaActionName;

        public BackoffQuantum(State rollback, State failover, State pre, String sagaActionName) {
            Assert.notNull(rollback, "task iState null");
            Assert.notNull(failover, "failover iState null");
            this.rollback         = rollback;
            this.rollbackFailover = failover;
            this.pre              = pre;
            this.sagaActionName   = sagaActionName;
        }

        public void onEvent(SagaProcessor processor, FsmContext ctx) throws StatusException, IdempotentException {
            State      state      = ctx.getState();
            SagaAction sagaAction = ctx.getAction();
            //如果任务可执行
            if (Objects.equals(state, rollback)) {
                //加锁
                if (!processor.tryLock(ctx)) {
                    return;
                }
                //设置容错
                processor.setFailover(ctx, rollbackFailover);
                //冥等
                processor.idempotent(ctx.getFlow().getName(), ctx.getId(), state, ctx.getEvent());
                //执行业务
                try {
                    sagaAction.rollback(ctx);
                } catch (Exception e) {
                    ctx.setState(rollbackFailover);
                } finally {
                    processor.unLock(ctx);
                    processor.onActionFinallyPlugin(ctx);
                    ctx.metricsNode(ctx);
                }
            } else if (Objects.equals(state, rollbackFailover)) {
                try {
                    Boolean queryResult = sagaAction.rollbackQuery(ctx);
                    //如果业务未发送成功，取消冥等
                    if (Objects.equals(queryResult, null)) {
                        processor.unidempotent(ctx.getFlow().getName(), ctx.getId(), state, ctx.getEvent());
                        ctx.setState(rollback);
                    }
                    if (queryResult) {
                        if (null == pre) {
                            ctx.setFlowStatus(FlowStatus.FINISH);
                        }
                    }
                    if (!queryResult) {
                        //失败补偿策略:反复执行。直接失败
                        Integer retryTimes       = ctx.getProfile().getStateAttrs().get(rollback).getRetry();
                        Integer executeTimeState = processor.executeTimes(ctx, rollback);
                        if (executeTimeState > retryTimes) {
                            //失败处理机制：前向转后向，后向转人工
                            ctx.setFlowStatus(FlowStatus.MANUAL);
                        } else {
                            //如果可以重试，则设置为初始状态，重新执行任务。
                            ctx.setState(rollback);
                        }
                    }
                } catch (NoSuchRecordException e) {
                    processor.unidempotent(ctx.getFlow().getName(), ctx.getId(), state, ctx.getEvent());
                    ctx.setState(rollback);
                } catch (Exception e) {
                    ctx.setState(rollbackFailover);
                } finally {
                    processor.onActionFinallyPlugin(ctx);
                    ctx.metricsNode(ctx);
                }
            }
        }
    }


}
