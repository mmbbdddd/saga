package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.Payload;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.factory.SagaFlowFactory;
import cn.hz.ddbm.pc.newcore.utils.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SagaProcessor extends ProcesorService<SagaContext> {


    public void afterPropertiesSet() {
        initParent();
        SpringUtil.getBeansOfType(SagaFlowFactory.class).forEach((key, flowFactory) -> {
            this.flows.putAll(flowFactory.getFlows());
        });
    }

    @Override
    public SagaContext getContext(String flowName, Payload payload) throws SessionException {
        Assert.notNull(flowName, "flowName is null");
        Assert.notNull(payload, "payload is null");
        SagaFlow            flow    = (SagaFlow) getFlow(flowName);
        Map<String, Object> session = getSession(flowName, payload.getId());
        SagaContext         ctx     = new SagaContext<>(flow, payload, session);
        return ctx;
    }

    public void workerProcess(SagaContext ctx) throws FlowEndException, InterruptedException, PauseException, RetryableException {
        Assert.notNull(ctx, "ctx is null");
        ctx.setProcessor(this);
        SagaFlow  flow       = (SagaFlow) ctx.getFlow();
        SagaState state      = (SagaState) ctx.getState();
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

        SagaWorker worker = flow.getWorker(state.getIndex());
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
//            add(new SagaDigestPlugin());
        }};
    }


}
