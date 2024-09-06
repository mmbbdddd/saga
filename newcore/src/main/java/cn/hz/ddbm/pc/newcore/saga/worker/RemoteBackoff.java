package cn.hz.ddbm.pc.newcore.saga.worker;

import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.LockException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaAction;

import java.util.Objects;

public class RemoteBackoff<S extends Enum<S>> {
    //    当前节点状态（回滚）
    SagaState<S> rollback;
    //    当前节点成功to状态
    SagaState<S> rollbackFailover;
    //    当前节点成功to状态（回滚到前一个节点）
    SagaState<S> pre;
    //    当前节点异常to重试状态
    SagaState<S> retry;
    //    当前节点失败到人工状态
    FlowStatus   manual;

    public RemoteBackoff(S pre, S current) {
        this.rollback         = new SagaState<>(current, SagaState.Offset.task, false);
        this.rollbackFailover = new SagaState<>(current, SagaState.Offset.failover, false);
        this.retry            = new SagaState<>(current, SagaState.Offset.retry, false);
        this.pre              = new SagaState<>(pre, SagaState.Offset.task, false);
        this.manual           = FlowStatus.MANUAL;
    }

    public void execute(SagaContext<S> ctx) throws IdempotentException, LockException {
        ProcesorService  processor    = ctx.getProcessor();
        SagaState<S>     lastState    = ctx.getState().cloneSelf();
        RemoteSagaAction sagaAction   = (RemoteSagaAction) ctx.getAction();
        SagaState.Offset currentState = lastState.getOffset();
        //如果任务可执行
        if (Objects.equals(currentState, SagaState.Offset.task) || Objects.equals(currentState, SagaState.Offset.retry)) {
            //加锁
            processor.plugin().pre(ctx);
            //设置容错
            ctx.setState(rollbackFailover);
            processor.updateStatus(ctx);
            //冥等
            processor.idempotent(ctx);
            //执行业务
            try {
                sagaAction.rollback(ctx);
                processor.plugin().post(lastState, ctx);
            } catch (Exception e) {
                processor.plugin().error(lastState, e, ctx);
//                ctx.setState(failover);
            } finally {
                processor.plugin()._finally(ctx);
                processor.metricsNode(ctx);
            }
        } else if (Objects.equals(currentState, SagaState.Offset.failover)) {
            try {
                Boolean queryResult = sagaAction.rollbackQuery(ctx);
                //如果业务未发送成功，取消冥等，设置为任务可执行状态
                if (queryResult == null) {
                    processor.unidempotent(ctx);
                    ctx.setState(rollback);
                } else if (!queryResult) {   //业务不成功
                    processor.unidempotent(ctx);
                    //失败补偿策略:反复执行。直接失败
                    Integer retryTimes       = ctx.getFlow().getRetry(lastState);
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
                processor.unidempotent(ctx);
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
