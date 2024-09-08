package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Payload;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.factory.SagaFlowFactory;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.utils.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SagaProcessor<E extends Enum<E>> extends ProcesorService<SagaState<E>, FlowContext<SagaFlow<E>, SagaState<E>, SagaWorker<E>>> {


    public void afterPropertiesSet() {
        initParent();
        SpringUtil.getBeansOfType(SagaFlowFactory.class).forEach((key, flowFactory) -> {
            this.flows.putAll(flowFactory.getFlows());
        });
    }

    @Override
    public FlowContext<SagaFlow<E>, SagaState<E>, SagaWorker<E>> getContext(String flowName, Payload<SagaState<E>> payload) throws SessionException {
        Assert.notNull(flowName, "flowName is null");
        Assert.notNull(payload, "payload is null");
        SagaFlow<E>                                           flow    = getFlow(flowName);
        Map<String, Object>                                   session = getSession(flowName, payload.getId());
        FlowContext<SagaFlow<E>, SagaState<E>, SagaWorker<E>> ctx     = new FlowContext<>(flow, payload, session);
        Logs.flow.debug("create context:{},{}", flow, payload);
        return ctx;
    }

    public void workerProcess(FlowContext<SagaFlow<E>, SagaState<E>, SagaWorker<E>> ctx) throws FlowEndException, InterruptedException, PauseException, RetryableException {
        Assert.notNull(ctx, "ctx is null");
        ctx.setProcessor(this);
        SagaFlow  flow       = ctx.getFlow();
        SagaState state      = ctx.getState();
        Integer   stateRetry = flow.getRetry(state);
        //状态不可执行
        if (flow.isEnd(state)) {
            throw new FlowEndException();
        }
        if (state.isPaused()) {
            throw new PauseException();
        }
        //工作流结束
        Long stateExecuteTimes = getExecuteTimes(ctx, state);
        if (stateExecuteTimes > stateRetry) {
            throw new InterruptedException(String.format("节点%s执行次数超限制{}>{}", state.code(), stateExecuteTimes, stateRetry));
        }

        SagaWorker worker = flow.getWorker(state.getIndex(),ctx);
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
//            add(new SagaDigestPlugin());
        }};
    }


}
