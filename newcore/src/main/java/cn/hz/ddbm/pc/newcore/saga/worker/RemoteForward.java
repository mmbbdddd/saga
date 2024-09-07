package cn.hz.ddbm.pc.newcore.saga.worker;

import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.OffsetState;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.SagaFlow;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaAction;

import java.util.Objects;

public class RemoteForward<S extends Enum<S>> {
    //    当前节点状态
    SagaState<S> task;
    //    当前节点错误to状态
    SagaState<S> failover;
    //    当前节点to成功状态
    SagaState<S> su;
    //    当前节点异常to重试状态
    SagaState<S> retry;
    //    当前节点回滚to状态
    SagaState<S> rollback;

    public RemoteForward(S pre, S current, S next) {
        this.task     = new SagaState<>(current, OffsetState.task, SagaState.Direction.forward);
        this.failover = new SagaState<>(current, OffsetState.failover, SagaState.Direction.forward);
//        到下一个节点
        this.su = null == next ? null : new SagaState<>(next, OffsetState.task, SagaState.Direction.forward);
//        到前一个节点
        this.rollback = null == pre ? null : new SagaState<>(pre, OffsetState.task, SagaState.Direction.backoff);
        this.retry    = new SagaState<>(current, OffsetState.retry, SagaState.Direction.forward);

    }

    public void execute(SagaContext<S> ctx) throws IdempotentException, ActionException, LockException, FlowEndException, NoSuchRecordException {
        ProcesorService  processor    = ctx.getProcessor();
        SagaState<S>     lastState    = ctx.getState().cloneSelf();
        RemoteSagaAction sagaAction   = (RemoteSagaAction) ctx.getAction();
        OffsetState      currentState = lastState.getOffset();
        //如果任务可执行
        if (Objects.equals(currentState, OffsetState.task) || Objects.equals(currentState, OffsetState.retry)) {
            //冥等
            processor.idempotent(ctx);
            processor.plugin().pre(ctx);
            //设置容错
            ctx.setState(failover);
            processor.updateStatus(ctx);

            //执行业务
            try {
                sagaAction.execute(ctx);
                processor.plugin().post(lastState, ctx);
            } catch (ActionException e) {
                processor.plugin().error(lastState, e, ctx);
                throw e;
            } catch (Exception e) {
                processor.plugin().error(lastState, e, ctx);
                throw new ActionException(e);
            } finally {
                processor.plugin()._finally(ctx);
                processor.metricsNode(ctx);
            }
        } else if (Objects.equals(currentState, OffsetState.failover)) {
            try {
                Boolean queryResult = sagaAction.executeQuery(ctx);
                //如果业务未发送成功，取消冥等，设置为任务可执行状态
                if (Objects.equals(queryResult, null)) {
                    processor.unidempotent(ctx);
                    ctx.setState(task);
                } else if (!queryResult) {   //业务不成功
                    processor.unidempotent(ctx);
                    //失败补偿策略:反复执行。直接失败
                    Integer retryTimes       = ctx.getFlow().getRetry(lastState);
                    Long    executeTimeState = processor.getExecuteTimes(ctx, lastState);
                    //超过重试次数，设置为失败，低于重试次数，设置为retry
                    if (executeTimeState > retryTimes) {
                        //失败处理机制：前向转后向，后向转人工
                        if (null == rollback) {
                            throw new FlowEndException();
                        } else {
                            ctx.setState(rollback);
                        }
                    } else {
                        //如果可以重试，则设置为初始状态，重新执行任务。
                        ctx.setState(retry);
                    }
                } else {
                    if (null == su) {
                        throw new FlowEndException();
                    } else {
                        ctx.setState(su);
                    }
                }
                processor.plugin().post(lastState, ctx);
            } catch (FlowEndException e) {
                ctx.setState(task);
                processor.plugin().error(lastState, e, ctx);
                throw e;
            } catch (NoSuchRecordException e) {
                ctx.setState(task);
                processor.plugin().error(lastState, e, ctx);
                throw e;
            } catch (ActionException e) {
                ctx.setState(failover);
                processor.plugin().error(lastState, e, ctx);
                throw e;
            } catch (Exception e) {
                ctx.setState(failover);
                processor.plugin().error(lastState, e, ctx);
                throw new ActionException(e);
            } finally {
                processor.plugin()._finally(ctx);
                processor.metricsNode(ctx);
            }
        }
    }
}
