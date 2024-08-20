package cn.hz.ddbm.pc.core.processor;


import cn.hz.ddbm.pc.core.*;
import cn.hz.ddbm.pc.core.enums.FlowStatus;
import cn.hz.ddbm.pc.core.exception.wrap.ActionException;
import cn.hz.ddbm.pc.core.exception.wrap.StatusException;
import cn.hz.ddbm.pc.core.support.StatusManager;
import cn.hz.ddbm.pc.core.utils.InfraUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @Description TODO
 * @Author wanglin
 * @Date 2024/8/7 23:32
 * @Version 1.0.0
 **/


public class SagaProcessor<S extends Enum<S>> extends BaseProcessor<Action.SagaAction<S>, S> {


    public SagaProcessor(Fsm.FsmRecord<S> f, List<Plugin> plugins) {
        super(f, plugins);
    }

    @Override
    public Action.SagaAction<S> action(FlowContext<S, ?> ctx) {
        return Action.of(getFsmRecord().getActionDsl(), Action.SagaAction.class, ctx);
    }

    public void execute(FlowContext<S, ?> ctx) throws ActionException, StatusException {
        Fsm<S>       flow     = ctx.getFlow();
        Serializable id       = ctx.getId();
        String       event    = ctx.getEvent();
        State<S>     lastNode = ctx.getStatus();
        try {
            StatusManager statusManager = getStatusManager(ctx);
            statusManager.setStatus(flow.getName(), id, new State<S>(getFsmRecord().getFailover(), FlowStatus.RUNNABLE), 10, ctx);
            ctx.getStatus().flush(event, getFsmRecord().getFailover(), flow);
        } catch (Exception e) {
            ctx.getStatus().flush(event, getFsmRecord().getFailover(), flow);
            throw new StatusException(e);
        }
        try {
            preActionPlugin(flow, ctx);
            S      currentState = action(ctx).query(ctx);
            Set<S> conditions   = getFsmRecord().getConditions();
            if (conditions.contains(currentState)) {
                action(ctx).execute(ctx);
                S nextNode = action(ctx).getExecuteResult(ctx);
                if (null == nextNode) {
                    nextNode = getFsmRecord().getFailover();
                }
                ctx.getStatus().flush(event, nextNode, flow);
            }
            postActionPlugin(flow, lastNode.getName(), ctx);
        } catch (ActionException e) {
            ctx.getStatus().flush(event, getFsmRecord().getFailover(), flow);
            onActionExceptionPlugin(flow, lastNode.getName(), e, ctx);
            throw e;
        } catch (Exception e) {
            ctx.getStatus().flush(event, getFsmRecord().getFailover(), flow);
            onActionExceptionPlugin(flow, lastNode.getName(), e, ctx);
            throw new ActionException(e);
        } finally {
            onActionFinallyPlugin(flow, ctx);
            ctx.metricsNode(ctx);
        }
    }

    private StatusManager getStatusManager(FlowContext<S, ?> ctx) {
        return InfraUtils.getStatusManager(ctx.getProfile().getStatusManager());
    }


}


