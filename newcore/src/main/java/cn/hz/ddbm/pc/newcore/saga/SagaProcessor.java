package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.Payload;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.factory.SagaFlowFactory;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.plugins.SagaDigestPlugin;
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

    public void workerProcess(SagaContext ctx) throws FlowEndException, InterruptedException, PauseException {
        Assert.notNull(ctx, "ctx is null");
        ctx.setProcessor(this);
        SagaFlow   flow       = (SagaFlow) ctx.getFlow();
        FlowStatus status     = ctx.getStatus();
        SagaState  state      = (SagaState) ctx.getState();
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
        Long stateExecuteTimes = getExecuteTimes(ctx, state);
        if (stateExecuteTimes > stateRetry) {
            throw new InterruptedException(String.format("节点%s执行次数超限制{}>{}", state.code(), stateExecuteTimes, stateRetry));
        }

        SagaWorker worker = flow.getWorker(state.getMaster());
        try {
            ctx.setWorker(worker);
            worker.execute(ctx);
        } catch (Throwable e) {
            try {
                if (ExceptionUtils.isInterrupted(e)) { //中断异常，暂停执行，等下一次事件触发
                    Logs.error.error("中断异常：{},{}", ctx.getFlow().getName(), ctx.getId(), ExceptionUtils.unwrap(e));
                    flush(ctx);
                } else if (ExceptionUtils.isRetryable(e)) { //中断异常，暂停执行，等下一次事件触发
                    Logs.flow.warn("可重试异常：{},{}", ctx.getFlow().getName(), ctx.getId(), ExceptionUtils.unwrap(e));
                    flush(ctx);
                } else if (ExceptionUtils.isPaused(e)) { //暂停异常，状态设置为暂停，等人工修复
                    Logs.error.error("暂停异常：{},{}", ctx.getFlow().getName(), ctx.getId(), ExceptionUtils.unwrap(e));
                    ctx.setStatus(FlowStatus.PAUSE);
                    flush(ctx);
                } else if (ExceptionUtils.isStoped(e)) {//流程结束或者取消
                    Logs.flow.info("{},{}", ctx.getFlow().getName(), ctx.getId(), ExceptionUtils.unwrap(e));
                    status = ctx.getFlow().getEnds().contains(ctx.getState()) ? FlowStatus.FINISH : ctx.getStatus();
                    ctx.setStatus(status);
                    flush(ctx);
                } else {
                    //可重试异常
                    Logs.error.error("不可预料的异常：{},{}", ctx.getFlow().getName(), ctx.getId(), ExceptionUtils.unwrap(e));
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
//            add(new SagaDigestPlugin());
        }};
    }


}
