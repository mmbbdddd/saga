package cn.hz.ddbm.pc.newcore.fsm.worker;

import cn.hz.ddbm.pc.newcore.OffsetState;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmProcessor;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.fsm.FsmWorker;
import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmActionProxy;
import cn.hz.ddbm.pc.newcore.fsm.router.LocalRouter;

import java.util.Objects;

public class FsmLocalWorker<S extends Enum<S>> extends FsmWorker<S> {
    FsmState<S>        from;
    LocalFsmActionProxy<S> action;
    LocalRouter<S>       router;

    public FsmLocalWorker(S from, Class<? extends LocalFsmAction> action, LocalRouter<S> router) {
        this.from   = FsmState.of(from);
        this.action = new LocalFsmActionProxy<>(action);
        this.router = router;
    }

    public void execute(FsmContext<S> ctx) throws StatusException, IdempotentException, ActionException, LockException, PauseException, FlowEndException, InterruptedException, ProcessingException, NoSuchRecordException {
        FsmProcessor<S> processor = (FsmProcessor<S>) ctx.getProcessor();
        ctx.setAction(action);
        ctx.setRouter(router);
        //如果任务可执行
        OffsetState offset = ctx.getState().getOffset();

        if (Objects.equals(offset, OffsetState.task)) {
            processor.plugin().pre(ctx);
            //todo jdbc transition
            //执行业务
            try {
                Object result = action.execute(ctx);
                S nextState = router.router(ctx, result);

                ctx.setState(FsmState.of(nextState));
                processor.plugin().post(from, ctx);
            } catch (ActionException e) {
                processor.plugin().error(from, e, ctx);
                throw e;
            } finally {
                //end transition
                processor.plugin()._finally(ctx);
                processor.metricsNode(ctx);
            }
        }
    }

}
