package cn.hz.ddbm.pc.newcore.fsm;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.FlowProcessorService;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.*;

public class FsmProcessor extends FlowProcessorService<FsmContext> {


    @Override
    public void workerProcess(FsmContext ctx) throws FlowEndException, InterruptedException, PauseException {
        Assert.notNull(ctx, "ctx is null");
        ctx.setProcessor(this);
        FsmModel   flow       = ctx.getFlow();
        FlowStatus status     = ctx.getStatus();
        FsmState   state      = ctx.getState();
        Integer    stateRetry = flow.getRetry(state);
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

        try {
            FsmWorker worker = flow.getWorker(ctx.getState(), Coast.FSM.EVENT_DEFAULT);
            ctx.setWorker(worker);
            ctx.setAction(worker.getAction().getOrInitAction());
            worker.execute(ctx);
        } catch (StatusException e) {
            throw e;
        } catch (IdempotentException e) {
            throw new InterruptedException(e);
        } catch (ActionException e) {
            throw new RuntimeException(e);
        } catch (TransitionNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
