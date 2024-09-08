package cn.hz.ddbm.pc.newcore.fsm;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Payload;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.factory.FsmFlowFactory;
import cn.hz.ddbm.pc.newcore.utils.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FsmProcessor<E extends Enum<E>> extends ProcesorService<FsmState<E>, FlowContext<FsmFlow<E>, FsmState<E>, FsmWorker<E>>> {
    public void afterPropertiesSet() {
        initParent();

        SpringUtil.getBeansOfType(FsmFlowFactory.class).forEach((key, flowFactory) -> {
            this.flows.putAll(flowFactory.getFlows());
        });
    }


    @Override
    public FlowContext<FsmFlow<E>, FsmState<E>, FsmWorker<E>> getContext(String flowName, Payload<FsmState<E>> payload) throws SessionException {
        Assert.notNull(flowName, "flowName is null");
        Assert.notNull(payload, "payload is null");
        FsmFlow<E>                                         flow    = getFlow(flowName);
        Map<String, Object>                                session = getSession(flowName, payload.getId());
        FlowContext<FsmFlow<E>, FsmState<E>, FsmWorker<E>> ctx     = new FlowContext<>(flow, payload, session);
        return ctx;
    }


    @Override
    public void workerProcess(FlowContext<FsmFlow<E>, FsmState<E>, FsmWorker<E>> ctx) throws FlowEndException, InterruptedException, PauseException, RetryableException {
        workerProcess(Coast.EVENT_DEFAULT, ctx);
    }

    public void workerProcess(String event, FlowContext<FsmFlow<E>, FsmState<E>, FsmWorker<E>> ctx) throws FlowEndException, InterruptedException, PauseException, RetryableException {
        Assert.notNull(ctx, "ctx is null");
        event = null == event ? Coast.EVENT_DEFAULT : event;
        ctx.setProcessor(this);
        FsmFlow<E>  flow       = ctx.getFlow();
        FsmState<E> state      = ctx.getState();
        Integer     stateRetry = flow.getRetry(state);
        //状态不可执行
        if (flow.isEnd(state)) {
            throw new FlowEndException();
        }
        if (state.isPaused()) {
            throw new PauseException();
        }
        //工作流结束
        if (flow.isEnd(state)) {
            throw new FlowEndException();
        }
        //工作流结束
        Long stateExecuteTimes = getExecuteTimes(ctx, state);
        if (stateExecuteTimes > stateRetry) {
            throw new InterruptedException(String.format("节点%s执行次数超限制%s>%s", state.code(), stateExecuteTimes, stateRetry));
        }
        FsmWorker<E> worker = flow.getWorker(ctx.getState(), event);
        try {
            ctx.setWorker(worker);
            worker.execute(ctx);
        } catch (Throwable e) {
            ExceptionUtils.wrap(e, ctx.getProfile());
        }
    }


    @Override
    protected List<Plugin> getDefaultPlugins() {
        return new ArrayList<Plugin>() {{
//            add(new FsmDigestPlugin());
        }};
    }


}
