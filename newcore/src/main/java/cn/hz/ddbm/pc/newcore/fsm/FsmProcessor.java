package cn.hz.ddbm.pc.newcore.fsm;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.OffsetState;
import cn.hz.ddbm.pc.newcore.Payload;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.factory.FsmFlowFactory;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.utils.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FsmProcessor<S extends Enum<S>> extends ProcesorService<FsmContext<S>> {
    public void afterPropertiesSet() {
        initParent();

        SpringUtil.getBeansOfType(FsmFlowFactory.class).forEach((key, flowFactory) -> {
            this.flows.putAll(flowFactory.getFlows());
        });
    }


    @Override
    public FsmContext<S> getContext(String flowName, Payload payload) throws SessionException {
        Assert.notNull(flowName, "flowName is null");
        Assert.notNull(payload, "payload is null");
        FsmFlow<S>          flow    = (FsmFlow<S>) getFlow(flowName);
        Map<String, Object> session = getSession(flowName, payload.getId());
        FsmContext<S>       ctx     = new FsmContext<>(flow, payload, session);
        return ctx;
    }


    @Override
    public void workerProcess(FsmContext<S> ctx) throws FlowEndException, InterruptedException, PauseException, RetryableException {
        workerProcess(Coast.FSM.EVENT_DEFAULT, ctx);
    }

    public void workerProcess(String event, FsmContext<S> ctx) throws FlowEndException, InterruptedException, PauseException, RetryableException {
        Assert.notNull(ctx, "ctx is null");
        event = null == event ? Coast.FSM.EVENT_DEFAULT : event;
        ctx.setProcessor(this);
        FsmFlow<S>  flow       = ctx.getFlow();
        FsmState<S> state      = ctx.getState();
        Integer     stateRetry = flow.getRetry(state);
        //状态不可执行
        if (state.isEnd(flow)) {
            throw new FlowEndException();
        }
        if (state.isPause()) {
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
        FsmWorker worker = null;
        worker = flow.getWorker(ctx.getState(), event);
        try {
            ctx.setWorker(worker);
            worker.execute(ctx);
        } catch (Throwable e) {
            ExceptionUtils.wrap(e,ctx.getProfile());
        }
    }


    @Override
    protected List<Plugin> getDefaultPlugins() {
        return new ArrayList<Plugin>() {{
//            add(new FsmDigestPlugin());
        }};
    }


}
