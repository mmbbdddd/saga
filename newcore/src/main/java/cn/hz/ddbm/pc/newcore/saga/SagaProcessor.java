package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.FlowProcessorService;
import cn.hz.ddbm.pc.PluginService;
import cn.hz.ddbm.pc.newcore.*;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.factory.SagaFlowFactory;
import cn.hz.ddbm.pc.newcore.infra.InfraUtils;
import cn.hz.ddbm.pc.newcore.infra.SessionManager;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.plugins.SagaDigestPlugin;
import cn.hz.ddbm.pc.newcore.utils.ExceptionUtils;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SagaProcessor<S> extends FlowProcessorService<SagaContext<S>> {

    @PostConstruct
    public void init(){
        SpringUtil.getBeansOfType(SagaFlowFactory.class).forEach((key, flowFactory) -> {
            this.flows.putAll(flowFactory.getFlows());
        });
    }

    public SagaContext<S> workerProcess(String flowName, SagaPayload<S> payload, Profile profile) throws FlowEndException, InterruptedException, PauseException, SessionException {
        Assert.notNull(flowName,"flowName is null");
        Assert.notNull(payload,"payload is null");
        SagaFlow<S>         flow    = (SagaFlow<S>) getFlow(flowName);
        Map<String, Object> session = getSession(flowName, payload.getId());
        SagaContext<S> ctx =  new SagaContext<>(flow, payload, profile, session);
        workerProcess(ctx);
        return ctx;
    }


    public void workerProcess(SagaContext<S> ctx) throws FlowEndException, InterruptedException, PauseException {
        Assert.notNull(ctx, "ctx is null");
        ctx.setProcessor(this);
        SagaFlow<S>  flow       = ctx.getFlow();
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
        Long stateExecuteTimes = getExecuteTimes(ctx, state);
        if (stateExecuteTimes > stateRetry) {
            throw new InterruptedException(String.format("节点%s执行次数超限制{}>{}", state.code(), stateExecuteTimes, stateRetry));
        }

        SagaWorker<S> worker = flow.getWorker(ctx.getState().getState());
        try {
            ctx.setWorker(worker);
            worker.execute(ctx);
        } catch (Throwable e) {
            try {
                if (isInterrupted(e, ctx)) { //中断异常，暂停执行，等下一次事件触发
                    Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), ExceptionUtils.unwrap(e));
                    flush(ctx);
                } else if (isPaused(e, ctx)) { //暂停异常，状态设置为暂停，等人工修复
                    Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), ExceptionUtils.unwrap(e));
                    ctx.setStatus(FlowStatus.PAUSE);
                    flush(ctx);
                } else if (isStoped(e, ctx)) {//流程结束或者取消
                    Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), ExceptionUtils.unwrap(e));
                    status = ctx.getFlow().getEnds().contains(ctx.getState()) ? FlowStatus.FINISH : ctx.getStatus();
                    ctx.setStatus(status);
                    flush(ctx);
                } else {
                    //可重试异常
                    Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), ExceptionUtils.unwrap(e));
                    Integer loopErrorTimes = ctx.getLoopErrorTimes().incrementAndGet();
                    if (loopErrorTimes > ctx.getProfile().getMaxLoopErrorTimes()) {
                        throw new InterruptedException(String.format("节点%s执行次数超限制%s>%s", state.code(), loopErrorTimes, ctx.getProfile()
                                .getMaxLoopErrorTimes()));
                    }
                    flush(ctx);
//                    ctx.getFlow().execute(ctx);
                }
            } catch (StatusException | SessionException e2) {
                Logs.status.error("", e2);
            }
        }
    }


    @Override
    protected List<Plugin> getDefaultPlugins() {
        return new ArrayList<Plugin>() {{
            add(new SagaDigestPlugin());
        }};
    }
}
