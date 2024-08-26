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
 *  业务一致性类业务通用流程
 *  1，设置状态位到容错位
 *  2，更新状态到状态管理器，如果成功则执行下一步
 *  3，条件执行业务逻辑
 *  4，执行成功+查询对方状态，更新状态
 *  5，更新成功，刷新状态
 *  6，异常，则充值状态位到容错（防45步骤错误）
 **/


public class SagaProcessor<S extends Enum<S>> extends BaseProcessor<Action.SagaAction<S>, S> {


    public SagaProcessor(Fsm.Transition<S> f, List<Plugin> plugins) {
        super(f, plugins);
    }

    @Override
    public Action.SagaAction<S> action(FsmContext<S, ?> ctx) {
        return Action.of(getFsmRecord().getActionDsl(),getFsmRecord().getFailover(), Action.SagaAction.class, ctx);
    }

    public void execute(FsmContext<S, ?> ctx) throws ActionException, StatusException {
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
                S nextNode = action(ctx).query(ctx);
                if (null == nextNode) {
                    nextNode = getFsmRecord().getFailover();
                }
                ctx.getStatus().flush(event, nextNode, flow);
            }
            postActionPlugin(flow, lastNode.getState(), ctx);
        } catch (ActionException e) {
            ctx.getStatus().flush(event, getFsmRecord().getFailover(), flow);
            onActionExceptionPlugin(flow, lastNode.getState(), e, ctx);
            throw e;
        } catch (Exception e) {
            ctx.getStatus().flush(event, getFsmRecord().getFailover(), flow);
            onActionExceptionPlugin(flow, lastNode.getState(), e, ctx);
            throw new ActionException(e);
        } finally {
            onActionFinallyPlugin(flow, ctx);
            ctx.metricsNode(ctx);
        }
    }

    private StatusManager getStatusManager(FsmContext<S, ?> ctx) {
        return InfraUtils.getStatusManager(ctx.getProfile().getStatusManager());
    }


}


