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

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SagaProcessor<E extends Enum<E>> extends ProcesorService<SagaState<E>, FlowContext<SagaFlow<E>, SagaState<E>, SagaWorker<E>>> {

    @PostConstruct
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
        Integer stateRetry  = flow.getRetry(state);
        //状态不可执行
        if (flow.isEnd(state)) {
            Logs.debug.info("状态{}.is {}", state, state.getStatus());
            throw new FlowEndException();
        }
        if (state.isPaused()) {
            Logs.debug.info("状态{}.isPaused", state);
            throw new PauseException();
        }
        //工作流结束
        Long stateExecuteTimes = getExecuteTimes(ctx, state);
        if (stateExecuteTimes > stateRetry) {
            Logs.debug.info("检测状态{} is over retry time:{}>{}", state, stateExecuteTimes, stateRetry);
            throw new InterruptedException("次数超限");
        }

        SagaWorker worker = flow.getWorker(state.getIndex());
        try {
            ctx.setWorker(worker);
            worker.execute(ctx);
            Logs.debug.info("状态变化{}>>>>{}", state, ctx.getState());
        } catch (Throwable e) {
            Logs.debug.info("状态异常{}", state, e);;
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
