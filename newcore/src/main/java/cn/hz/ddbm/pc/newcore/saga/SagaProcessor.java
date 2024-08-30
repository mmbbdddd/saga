package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.FlowProcessorService;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.*;

public class SagaProcessor<S> extends FlowProcessorService<SagaContext<S>> {


    @Override
    public void workerProcess(SagaContext<S> ctx) throws FlowEndException, InterruptedException, PauseException {
        Assert.notNull(ctx, "ctx is null");
        ctx.setProcessor(this);
        SagaModel<S> flow       = ctx.getFlow();
        FlowStatus   status     = ctx.getStatus();
        SagaState<S> state      = ctx.getState();
        Integer      stateRetry = flow.getRetry(state);
        //状态不可执行
        if (FlowStatus.isEnd(status)) {
            throw new FlowEndException();
        }
        if (FlowStatus.PAUSE.equals(status)) {
            throw new PauseException();
        }
        //工作流结束
        if (flow.isEnd(state)) {
            throw new FlowEndException();
        }
        //工作流结束
        Integer stateExecuteTimes = getStateExecuteTimes(ctx, flow.getName(), state);
        if (stateExecuteTimes > stateRetry) {
            throw new InterruptedException(String.format("节点%s执行次数超限制{}>{}", state.code(), stateExecuteTimes, stateRetry));
        }

        SagaWorker<S> worker = flow.getWorker(ctx.getState().getState());
        try {
            ctx.setWorker(worker);
            ctx.setAction(worker.getSagaAction().getOrInitAction());
            worker.execute(ctx);
        } catch (StatusException e) {
            throw new RuntimeException(e);
        } catch (IdempotentException e) {
            throw new RuntimeException(e);
        } catch (ActionException e) {
            throw new RuntimeException(e);
        }
    }


}
