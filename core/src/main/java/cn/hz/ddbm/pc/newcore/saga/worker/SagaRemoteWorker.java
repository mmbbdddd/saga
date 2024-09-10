package cn.hz.ddbm.pc.newcore.saga.worker;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaFlow;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaActionProxy;

import java.util.Objects;

public class SagaRemoteWorker<S extends Enum<S>> extends SagaWorker<S> {

    RemoteSagaActionProxy<S> action;
    SagaState<S>             failover;
    SagaState<S>             next;
    FlowStatus               su;
    SagaState<S>             rollback;
    SagaState<S>             rollbackFailover;
    SagaState<S>             pre;
    FlowStatus               fail;
    FlowStatus               manual;

    public SagaRemoteWorker(Integer index, Pair<S, Class<? extends SagaAction>> node ) {
        super(index, node.getKey());
        action                = new RemoteSagaActionProxy<>(node.getValue());
        this.failover         = new SagaState<>(index, SagaState.Offset.failover, FlowStatus.RUNNABLE);
        this.next             = new SagaState<>(index + 1, SagaState.Offset.task, FlowStatus.RUNNABLE);
        this.su               = FlowStatus.SU;
        this.next             = new SagaState<>(index - 1, SagaState.Offset.task, FlowStatus.RUNNABLE);
        this.rollback         = new SagaState<>(index, SagaState.Offset.rollback, FlowStatus.RUNNABLE);
        this.rollbackFailover = new SagaState<>(index, SagaState.Offset.rollbackFailover, FlowStatus.RUNNABLE);
        this.pre              = new SagaState<>(index - 1, SagaState.Offset.rollback, FlowStatus.RUNNABLE);
        this.fail             = FlowStatus.FAIL;
        this.manual           = FlowStatus.MANUAL;
    }


    public void execute(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws IdempotentException, ActionException, LockException, FlowEndException, NoSuchRecordException {
        ctx.setAction(action);
        SagaState        lastState = ctx.getState();
        SagaState.Offset offset    = lastState.getOffset();
        if (null == offset) {
            return;
        }
        switch (offset) {
            case task:
            case taskRetry:
                doAction(lastState, ctx);
                break;
            case failover:
                doActionFailover(lastState, ctx);
                break;
            case rollback:
            case rollbackRetry:
                rollbackAction(lastState, ctx);
                break;
            case rollbackFailover:
                doRollbackActionFailover(lastState, ctx);
                break;
        }
    }

    private void doRollbackActionFailover(SagaState lastState, FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws ActionException {
        ProcesorService processor = ctx.getProcessor();
        processor.plugin().pre(ctx);
        try {
            Boolean queryResult = action.remoteSagaRollbackFailover(ctx);
            //如果业务未发送成功，取消冥等，设置为任务可执行状态
            if (Objects.equals(queryResult, null)) {
                processor.unidempotent(ctx);
                ctx.getState().setOffset(SagaState.Offset.rollbackRetry);
//                ctx.setState(currentState);
            } else if (!queryResult) {   //业务不成功
                processor.unidempotent(ctx);
                ctx.getState().setStatus(manual);
            } else {//如果成功，设置为pre，如果pre为空，设置fail
                if (null != pre) {
                    ctx.setState(pre);
                } else {
                    ctx.getState().setStatus(fail);
                }
            }
            processor.plugin().post(lastState, ctx);
        } catch (NoSuchRecordException e) {
            processor.plugin().error(lastState, e, ctx);
            try {
                processor.unidempotent(ctx);
            } catch (IdempotentException ex) {
                Logs.error.error("取消冥等异常", e);
            }
            ctx.getState().setOffset(SagaState.Offset.taskRetry);
        } catch (IdempotentException e) {
            processor.plugin().error(lastState, e, ctx);
            Logs.error.error("取消冥等异常", e);
        } catch (ActionException e) {
            processor.plugin().error(lastState, e, ctx);
            throw e;
        } finally {
            processor.plugin()._finally(lastState, ctx);
            processor.metricsNode(ctx);
        }
    }

    private void rollbackAction(SagaState lastState, FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws ActionException, IdempotentException {
        ProcesorService processor = ctx.getProcessor();
        processor.plugin().pre(ctx);
        //冥等
        processor.idempotent(ctx);
        //设置容错
        ctx.setState(rollbackFailover);
        processor.updateStatus(ctx);
        //执行业务
        try {
            action.remoteSagaRollback(ctx);
            processor.plugin().post(lastState, ctx);
        } catch (ActionException e) {
            processor.plugin().error(lastState, e, ctx);
            throw e;
        } finally {
            processor.plugin()._finally(lastState, ctx);
            processor.metricsNode(ctx);
        }
    }

    private void doActionFailover(SagaState lastState, FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws ActionException {
        ProcesorService processor = ctx.getProcessor();
        processor.plugin().pre(ctx);
        try {
            //失败补偿策略:反复执行。直接失败
            Boolean queryResult = action.remoteSagaQuery(ctx);
            //如果业务未发送成功，取消冥等，设置为任务可执行状态

            if (Objects.equals(queryResult, null)) {
                processor.unidempotent(ctx);
                ctx.getState().setOffset(SagaState.Offset.taskRetry);
//                ctx.setState(currentState);
            } else if (!queryResult) {   //业务不成功
                processor.unidempotent(ctx);
                ctx.setState(rollback);
            } else {//如果成功，设置为下一个，如果下一个为空，设置为成功
                if (null != next) {
                    ctx.setState(next);
                } else {
                    ctx.getState().setStatus(su);
                }
            }
            processor.plugin().post(lastState, ctx);
        } catch (NoSuchRecordException e) {
            processor.plugin().error(lastState, e, ctx);
            try {
                processor.unidempotent(ctx);
            } catch (IdempotentException ex) {
                Logs.error.error("取消冥等异常", e);
            }
            ctx.getState().setOffset(SagaState.Offset.taskRetry);
        } catch (IdempotentException e) {
            Logs.error.error("取消冥等异常", e);
            processor.plugin().error(lastState, e, ctx);
        } catch (ActionException e) {
            e.printStackTrace();
            processor.plugin().error(lastState, e, ctx);
            throw e;
        } finally {
            processor.plugin()._finally(lastState, ctx);
            processor.metricsNode(ctx);
        }
    }

    private void doAction(SagaState lastState, FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws ActionException, IdempotentException {
        ProcesorService processor = ctx.getProcessor();
        processor.plugin().pre(ctx);
        //冥等
        processor.idempotent(ctx);
        //设置容错
        ctx.setState(failover);
        processor.updateStatus(ctx);
        //执行业务
        try {
            action.remoteSaga(ctx);
            processor.plugin().post(lastState, ctx);
        } catch (ActionException e) {
            processor.plugin().error(lastState, e, ctx);
            throw e;
        } finally {
            processor.plugin()._finally(lastState, ctx);
            processor.metricsNode(ctx);
        }
    }
}
