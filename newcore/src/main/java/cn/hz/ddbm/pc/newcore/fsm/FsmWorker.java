package cn.hz.ddbm.pc.newcore.fsm;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.Worker;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.saga.SagaProcessor;
import lombok.Data;

import java.util.Objects;

@Data
public class FsmWorker<S extends Enum<S>> extends Worker<FsmContext<S>> {
    FsmState<S>       from;
    FsmActionProxy<S> action;
    FsmRouter<S>      router;

    public FsmWorker(S from, Class<? extends FsmAction> actionClass, FsmRouter<S> router) {
        this.from   = new FsmState<>(from, FsmState.Offset.task);
        this.action = new FsmActionProxy(actionClass);
        this.router = router;
    }

    @Override
    public void execute(FsmContext<S> ctx) throws StatusException, IdempotentException, ActionException, LockException, PauseException, FlowEndException, InterruptedException, ProcessingException, NoSuchRecordException {
        FsmProcessor<S> processor = (FsmProcessor<S>) ctx.getProcessor();
        ctx.setAction(action);
        //如果任务可执行
        FsmState<S>     lastSate = ctx.getState();
        FsmState.Offset offset   = ctx.getState().offset;

        FsmState<S> failover = new FsmState<>(from.state, FsmState.Offset.failover);
        if (Objects.equals(offset, FsmState.Offset.task)) {
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
                action.execute(ctx);
                processor.plugin().post(from, ctx);
            } catch (ActionException e) {
                processor.plugin().error(from, e, ctx);
                throw e;
            } finally {
                processor.unLock(ctx);
                processor.plugin()._finally(ctx);
                processor.metricsNode(ctx);
            }
        } else if (Objects.equals(offset, FsmState.Offset.failover)) {
            try {
                Object queryResult = action.executeQuery(ctx);
                Assert.notNull(queryResult, "queryResult is null");
                S nextState = router.router(ctx, queryResult);
                //如果业务未发送成功，取消冥等，设置为任务可执行状态
                //业务有返回
                if (!ctx.getFlow().isRightState(FsmState.of(nextState))) {
                    throw new IllegalArgumentException("queryResult[" + queryResult + "] not a right state code");
                }
                ctx.setState(FsmState.of(nextState));
                processor.plugin().post(lastSate, ctx);
            } catch (NoSuchRecordException e) {
                ctx.setState(failover);
                processor.plugin().error(lastSate, e, ctx);
                throw e;
            } catch (ProcessingException e) {
                ctx.setState(failover);
                processor.plugin().error(lastSate, e, ctx);
                throw e;
            } catch (ActionException e) {
                ctx.setState(failover);
                processor.plugin().error(lastSate, e, ctx);
                throw e;
            } finally {
                processor.plugin()._finally(ctx);
                processor.metricsNode(ctx);
            }
        }
    }

}
