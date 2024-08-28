package cn.hz.ddbm.pc.core.processor;


import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.core.*;
import cn.hz.ddbm.pc.core.action.SagaAction;
import cn.hz.ddbm.pc.core.enums.FlowStatus;
import cn.hz.ddbm.pc.core.exception.ActionException;
import cn.hz.ddbm.pc.core.exception.wrap.StatusException;
import cn.hz.ddbm.pc.core.support.StatusManager;
import cn.hz.ddbm.pc.core.utils.InfraUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 业务一致性类业务通用流程
 * 1，设置状态位到容错位
 * 2，更新状态到状态管理器，如果成功则执行下一步
 * 3，条件执行业务逻辑
 * 4，执行成功+查询对方状态，更新状态
 * 5，更新成功，刷新状态
 * 6，异常，则充值状态位到容错（防45步骤错误）
 **/


public class SagaProcessor<S extends Enum<S>> extends BaseProcessor<SagaAction<S>, S> {


    public SagaProcessor(Fsm.Transition<S> f, List<Plugin> plugins) {
        super(f, plugins);
    }

    @Override
    public SagaAction<S> action(FsmContext<S, ?> ctx) {
        return Actions.typeOf(getFsmRecord(), SagaAction.class, ctx.getMockBean());
    }

    public void execute(FsmContext<S, ?> ctx) throws StatusException, ActionException {
        Fsm<S>       flow     = ctx.getFlow();
        Serializable id       = ctx.getId();
        String       event    = ctx.getEvent();
        S            lastNode = ctx.getState();
        try {
            StatusManager statusManager = getStatusManager(ctx);
            statusManager.setStatus(flow.getName(), id, Pair.of(FlowStatus.RUNNABLE,getFsmRecord().getFailover()), 10, ctx);
            ctx.setState( getFsmRecord().getFailover());
        } catch (Exception e) {
            ctx.setState( getFsmRecord().getFailover());
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
                ctx.setState( nextNode);
            }
            postActionPlugin(flow, lastNode, ctx);
        } catch (Exception e) {
            ctx.setState( getFsmRecord().getFailover());
            onActionExceptionPlugin(flow, lastNode, e, ctx);
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


