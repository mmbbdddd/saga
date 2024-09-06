package cn.hz.ddbm.pc.newcore.fsm.worker;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmProcessor;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.fsm.FsmWorker;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmActionProxy;
import cn.hz.ddbm.pc.newcore.fsm.router.RemoteRouter;

import java.util.Objects;

public class FsmRemoteWorker<S extends Enum<S>> extends FsmWorker<S> {
    FsmState<S>             from;
    RemoteFsmActionProxy<S> action;
    RemoteRouter<S>         router;

    public FsmRemoteWorker(S from, Class<? extends RemoteFsmAction> action, RemoteRouter<S> router) {
        this.from   = FsmState.of(from);
        this.action = new RemoteFsmActionProxy<>(action);
        this.router = router;
    }

    public void execute(FsmContext<S> ctx) throws StatusException, IdempotentException, ActionException, LockException, PauseException, FlowEndException, InterruptedException, ProcessingException, NoSuchRecordException {
        FsmProcessor<S> processor = (FsmProcessor<S>) ctx.getProcessor();
        ctx.setAction(action);
        //如果任务可执行
        FsmState<S>     lastSate = ctx.getState();
        FsmState.Offset offset   = ctx.getState().getOffset();

        FsmState<S> failover = new FsmState<>(from.getState(), FsmState.Offset.failover);
        if (Objects.equals(offset, FsmState.Offset.task)) {
            //加锁
            processor.tryLock(ctx);

            processor.plugin().pre(ctx);
            //设置容错
            ctx.setState(failover);
            processor.updateStatus(ctx);
            //冥等
            processor.idempotent( ctx);
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
                Object actionResult = action.executeQuery(ctx);
                Assert.notNull(actionResult, "queryResult is null");
                S nextState = router.router(ctx, actionResult);
                //如果业务未发送成功，取消冥等，设置为任务可执行状态
                //业务有返回
                if (!ctx.getFlow().isRightState(FsmState.of(nextState))) {
                    throw new IllegalArgumentException("ActionResult[" + actionResult + "] not a right state code");
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
