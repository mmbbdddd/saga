package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.FlowProcessorService;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.Worker;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.LockException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import lombok.Data;

import java.util.Objects;

@Data
public class SagaWorker<S extends Enum<S>> extends Worker<SagaContext<S>> {
    Integer           index;
    S                 currentState;
    ForwardQuantum<S> forward;
    BackoffQuantum<S> backoff;
    String            action;

    public SagaWorker(Integer index, S pre, S task, S next, String sagaAction) {
        Assert.notNull(task, "task is null");
        this.index        = index;
        this.currentState = task;
        this.forward      = new ForwardQuantum<>(task, next);
        this.backoff      = new BackoffQuantum<>(task, pre);
        this.action       = sagaAction;
    }

    @Override
    public void execute(SagaContext<S> ctx) throws IdempotentException, ActionException, LockException {
        FlowProcessorService processor = ctx.getProcessor();
        ctx.setAction((SagaAction) processor.getAction(action, SagaAction.class));
        SagaAction sagaAction = (SagaAction) ctx.getAction();
        ctx.setAction(sagaAction);
        if (ctx.getState().getIsForward()) {
            forward.onEvent(ctx);
        } else {
            backoff.onEvent(ctx);
        }
    }

    @Override
    public String toString() {
        return "SagaWorker{" + "index:" + index + ", currentState:" + currentState + '}';
    }
}

@Data
class ForwardQuantum<S extends Enum<S>> {
    SagaState<S> task;
    SagaState<S> failover;
    SagaState<S> su;
    SagaState<S> rollback;
    SagaState<S> retry;

    public ForwardQuantum(S curr, S next) {
        this.task     = new SagaState<>(curr, SagaState.Offset.task, true);
        this.failover = new SagaState<>(curr, SagaState.Offset.failover, true);
        this.su       = null != next ? new SagaState<>(next, SagaState.Offset.task, true) : new SagaState<>(curr, SagaState.Offset.su, true);
        this.retry    = new SagaState<>(curr, SagaState.Offset.retry, true);
        this.rollback = new SagaState<>(curr, SagaState.Offset.task, false);
    }

    public void onEvent(SagaContext<S> ctx) throws IdempotentException, ActionException, LockException {
        FlowProcessorService processor    = ctx.getProcessor();
        SagaState<S>         lastState    = ctx.getState().cloneSelf();
        SagaAction           sagaAction   = (SagaAction) ctx.getAction();
        SagaState.Offset     currentState = lastState.getOffset();
        //如果任务可执行
        if (Objects.equals(currentState, SagaState.Offset.task) || Objects.equals(currentState, SagaState.Offset.retry)) {
            //加锁
            processor.tryLock(ctx);
            processor.plugin().pre(ctx);
            //设置容错
            ctx.setState(failover);
            processor.updateStatus(ctx);
            //冥等
            processor.idempotent(ctx.getAction().code(), ctx);
            //执行业务
            try {
                sagaAction.execute(ctx);
                processor.plugin().post(lastState, ctx);
            } catch (ActionException e) {
                processor.plugin().error(lastState, e, ctx);
                throw e;
            } finally {
                processor.unLock(ctx);
                processor.plugin()._finally(ctx);
                processor.metricsNode(ctx);
            }
        } else if (Objects.equals(currentState, SagaState.Offset.failover)) {
            try {
                Boolean queryResult = sagaAction.executeQuery(ctx);
                //如果业务未发送成功，取消冥等，设置为任务可执行状态
                if (Objects.equals(queryResult, null)) {
                    processor.unidempotent(ctx.getAction().code(), ctx);
                    ctx.setState(task);
                } else if (!queryResult) {   //业务不成功
                    processor.unidempotent(ctx.getAction().code(), ctx);
                    //失败补偿策略:反复执行。直接失败
                    Integer retryTimes       = ctx.getRetry(lastState);
                    Long    executeTimeState = processor.getExecuteTimes(ctx, lastState);
                    //超过重试次数，设置为失败，低于重试次数，设置为retry
                    if (executeTimeState > retryTimes) {
                        //失败处理机制：前向转后向，后向转人工
                        ctx.setState(rollback);
                    } else {
                        //如果可以重试，则设置为初始状态，重新执行任务。
                        ctx.setState(retry);
                    }
                } else {
                    if (null == su) {
                        ctx.setStatus(FlowStatus.FINISH);
                    } else {
                        ctx.setState(su);
                    }
                }
                processor.plugin().post(lastState, ctx);
            } catch (NoSuchRecordException e) {
                processor.unidempotent(ctx.getAction().code(), ctx);
                ctx.setState(task);
                processor.plugin().error(lastState, e, ctx);
            } catch (ActionException e) {
                ctx.setState(failover);
                processor.plugin().error(lastState, e, ctx);
                throw e;
            } finally {
                processor.plugin()._finally(ctx);
                processor.metricsNode(ctx);
            }
        }
    }


}

@Data
class BackoffQuantum<S extends Enum<S>> {
    SagaState<S> rollback;
    SagaState<S> rollbackFailover;
    SagaState<S> pre;
    SagaState<S> retry;
    FlowStatus   manual;

    public BackoffQuantum(S rollback, S pre) {
        this.rollback         = new SagaState<>(rollback, SagaState.Offset.task, false);
        this.rollbackFailover = new SagaState<>(rollback, SagaState.Offset.failover, false);
        this.pre              = null != pre ? new SagaState<>(pre, SagaState.Offset.task, false) : new SagaState<>(rollback, SagaState.Offset.fail, false);
        this.retry            = new SagaState<>(rollback, SagaState.Offset.retry, false);
        this.manual           = FlowStatus.MANUAL;
    }

    public void onEvent(SagaContext<S> ctx) throws IdempotentException, LockException {
        FlowProcessorService processor    = ctx.getProcessor();
        SagaState<S>         lastState    = ctx.getState().cloneSelf();
        SagaAction           sagaAction   = (SagaAction) ctx.getAction();
        SagaState.Offset     currentState = lastState.getOffset();
        //如果任务可执行
        if (Objects.equals(currentState, SagaState.Offset.task) || Objects.equals(currentState, SagaState.Offset.retry)) {
            //加锁
            processor.tryLock(ctx);
            processor.plugin().pre(ctx);
            //设置容错
            ctx.setState(rollbackFailover);
            processor.updateStatus(ctx);
            //冥等
            processor.idempotent(ctx.getAction().code(), ctx);
            //执行业务
            try {
                sagaAction.rollback(ctx);
                processor.plugin().post(lastState, ctx);
            } catch (Exception e) {
                processor.plugin().error(lastState, e, ctx);
//                ctx.setState(failover);
            } finally {
                processor.unLock(ctx);
                processor.plugin()._finally(ctx);
                processor.metricsNode(ctx);
            }
        } else if (Objects.equals(currentState, SagaState.Offset.failover)) {
            try {
                Boolean queryResult = sagaAction.rollbackQuery(ctx);
                //如果业务未发送成功，取消冥等，设置为任务可执行状态
                if (queryResult == null) {
                    processor.unidempotent(ctx.getAction().code(), ctx);
                    ctx.setState(rollback);
                } else if (!queryResult) {   //业务不成功
                    processor.unidempotent(ctx.getAction().code(), ctx);
                    //失败补偿策略:反复执行。直接失败
                    Integer retryTimes       = ctx.getRetry(lastState);
                    Long    executeTimeState = processor.getExecuteTimes(ctx, lastState);
                    //超过重试次数，设置为失败，低于重试次数，设置为retry
                    if (executeTimeState > retryTimes) {
                        //失败处理机制：前向转后向，后向转人工
                        ctx.setStatus(manual);
                    } else {
                        //如果可以重试，则设置为初始状态，重新执行任务。
                        ctx.setState(retry);
                    }
                } else {
                    if (null == pre) {
                        ctx.setStatus(FlowStatus.FINISH);
                    } else {
                        ctx.setState(pre);
                    }
                }
                processor.plugin().post(lastState, ctx);
            } catch (NoSuchRecordException e) {
                processor.unidempotent(ctx.getAction().code(), ctx);
                ctx.setState(rollback);
                processor.plugin().error(lastState, e, ctx);
            } catch (Exception e) {
                ctx.setState(rollbackFailover);
                processor.plugin().error(lastState, e, ctx);
            } finally {
                processor.plugin()._finally(ctx);
                processor.metricsNode(ctx);
            }
        }

    }
}
