package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.Worker;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import lombok.Data;

import java.util.Objects;

@Data
public class SagaWorker<S> extends Worker<SagaContext<S>> {
    Integer           index;
    S                 currentState;
    ForwardQuantum<S> forward;
    BackoffQuantum<S> backoff;
    SagaActionProxy   sagaAction;

    public SagaWorker(Integer index, S pre, S task, S next, String sagaAction) {
        Assert.notNull(task, "task is null");
        this.index        = index;
        this.currentState = task;
        this.forward      = new ForwardQuantum<>(task, next);
        this.backoff      = new BackoffQuantum<>(task, pre);
        this.sagaAction   = new SagaActionProxy(sagaAction);
    }

    @Override
    public void execute(SagaContext<S> ctx) throws IdempotentException, ActionException {
        if (ctx.getState().getIsForward()) {
            forward.onEvent((SagaProcessor) ctx.getProcessor(), ctx);
        } else {
            backoff.onEvent((SagaProcessor) ctx.getProcessor(), ctx);
        }
    }

    @Override
    public String toString() {
        return "SagaWorker{" + "index:" + index + ", currentState:" + currentState + '}';
    }
}

@Data
class ForwardQuantum<S> {
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

    public void onEvent(SagaProcessor processor, SagaContext<S> ctx) throws IdempotentException, ActionException {

        SagaState<S>     lastState    = ctx.getState().cloneSelf();
        SagaActionProxy  sagaAction   = (SagaActionProxy) ctx.getAction();
        SagaState.Offset currentState = lastState.getOffset();
        //如果任务可执行
        if (Objects.equals(currentState, SagaState.Offset.task) || Objects.equals(currentState, SagaState.Offset.retry)) {
            //加锁
            if (!processor.tryLock(ctx)) {
                return;
            }
            processor.plugin().pre(ctx);
            //设置容错
            ctx.setState(failover);
            processor.updateStatus(ctx);
            //冥等
            processor.idempotent(ctx.getFlow().getName(), ctx.getId(), lastState, ctx.getEvent());
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
                ctx.metricsNode(ctx);
            }
        } else if (Objects.equals(currentState, SagaState.Offset.failover)) {
            try {
                Boolean queryResult = sagaAction.executeQuery(ctx);
                //如果业务未发送成功，取消冥等，设置为任务可执行状态
                if (Objects.equals(queryResult, null)) {
                    processor.unidempotent(ctx.getFlow().getName(), ctx.getId(), lastState, ctx.getEvent());
                    ctx.setState(task);
                }
                //业务不成功
                if (!queryResult) {
                    processor.unidempotent(ctx.getFlow().getName(), ctx.getId(), lastState, ctx.getEvent());
                    //失败补偿策略:反复执行。直接失败
                    Integer retryTimes       = ctx.getRetry(lastState);
                    Integer executeTimeState = processor.getStateExecuteTimes(ctx, ctx.getFlow().getName(), lastState);
                    //超过重试次数，设置为失败，低于重试次数，设置为retry
                    if (executeTimeState > retryTimes) {
                        //失败处理机制：前向转后向，后向转人工
                        ctx.setState(rollback);
                    } else {
                        //如果可以重试，则设置为初始状态，重新执行任务。
                        ctx.setState(retry);
                    }
                }
                if (queryResult) {
                    if (null == su) {
                        ctx.setStatus(FlowStatus.FINISH);
                    } else {
                        ctx.setState(su);
                    }
                }
                processor.plugin().post(lastState, ctx);
            } catch (NoSuchRecordException e) {
                processor.unidempotent(ctx.getFlow().getName(), ctx.getId(), lastState, ctx.getEvent());
                ctx.setState(task);
                processor.plugin().error(lastState, e, ctx);
            } catch (ActionException e) {
                ctx.setState(failover);
                processor.plugin().error(lastState, e, ctx);
                throw e;
            } finally {
                processor.plugin()._finally(ctx);
                ctx.metricsNode(ctx);
            }
        }
    }


}

@Data
class BackoffQuantum<S> {
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

    public void onEvent(SagaProcessor processor, SagaContext<S> ctx) throws IdempotentException {

        SagaState<S>     lastState    = ctx.getState().cloneSelf();
        SagaActionProxy  sagaAction   = (SagaActionProxy) ctx.getAction();
        SagaState.Offset currentState = lastState.getOffset();
        //如果任务可执行
        if (Objects.equals(currentState, SagaState.Offset.task) || Objects.equals(currentState, SagaState.Offset.retry)) {
            //加锁
            if (!processor.tryLock(ctx)) {
                return;
            }
            processor.plugin().pre(ctx);
            //设置容错
            ctx.setState(rollbackFailover);
            processor.updateStatus(ctx);
            //冥等
            processor.idempotent(ctx.getFlow().getName(), ctx.getId(), lastState, ctx.getEvent());
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
                ctx.metricsNode(ctx);
            }
        } else if (Objects.equals(currentState, SagaState.Offset.failover)) {
            try {
                Boolean queryResult = sagaAction.rollbackQuery(ctx);
                //如果业务未发送成功，取消冥等，设置为任务可执行状态
                if (Objects.equals(queryResult, null)) {
                    processor.unidempotent(ctx.getFlow().getName(), ctx.getId(), lastState, ctx.getEvent());
                    ctx.setState(rollback);
                }
                //业务不成功
                if (!queryResult) {
                    processor.unidempotent(ctx.getFlow().getName(), ctx.getId(), lastState, ctx.getEvent());
                    //失败补偿策略:反复执行。直接失败
                    Integer retryTimes       = ctx.getRetry(lastState);
                    Integer executeTimeState = processor.getStateExecuteTimes(ctx, ctx.getFlow().getName(), lastState);
                    //超过重试次数，设置为失败，低于重试次数，设置为retry
                    if (executeTimeState > retryTimes) {
                        //失败处理机制：前向转后向，后向转人工
                        ctx.setStatus(manual);
                    } else {
                        //如果可以重试，则设置为初始状态，重新执行任务。
                        ctx.setState(retry);
                    }
                }
                if (queryResult) {
                    if (null == pre) {
                        ctx.setStatus(FlowStatus.FINISH);
                    } else {
                        ctx.setState(pre);
                    }
                }
                processor.plugin().post(lastState, ctx);
            } catch (NoSuchRecordException e) {
                processor.unidempotent(ctx.getFlow().getName(), ctx.getId(), lastState, ctx.getEvent());
                ctx.setState(rollback);
                processor.plugin().error(lastState, e, ctx);
            } catch (Exception e) {
                ctx.setState(rollbackFailover);
                processor.plugin().error(lastState, e, ctx);
            } finally {
                processor.plugin()._finally(ctx);
                ctx.metricsNode(ctx);
            }
        }

    }
}
