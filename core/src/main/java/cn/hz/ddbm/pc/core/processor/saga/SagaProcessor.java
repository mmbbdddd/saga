package cn.hz.ddbm.pc.core.processor.saga;


import cn.hz.ddbm.pc.core.*;
import cn.hz.ddbm.pc.core.action.SagaAction;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.exception.ActionException;
import cn.hz.ddbm.pc.core.exception.IdempotentException;
import cn.hz.ddbm.pc.core.exception.StatusException;
import cn.hz.ddbm.pc.core.support.StatusManager;
import cn.hz.ddbm.pc.core.utils.InfraUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 业务严格一致性saga执行流程
 * 1，设置状态位到容错位
 * 2，更新状态到状态管理器，如果成功则执行下一步
 * 3，条件执行业务逻辑
 * 4，执行成功+查询对方状态，更新状态
 * 5，更新成功，刷新状态
 * 6，异常，则充值状态位到容错（防45步骤错误）
 **/


public class SagaProcessor extends Processor {
    Transition transition;

    public SagaProcessor(Transition transition, List<Plugin> plugins) {
        super(plugins);
        this.transition = transition;
    }

    public SagaAction createAction(FsmContext ctx) {
        return Actions.typeOf(transition.getActionDsl(), SagaAction.class, ctx.getMockBean());
    }

    public void execute(FsmContext ctx) throws StatusException, ActionException, IdempotentException {
        transition.getHandler().onEvent(this, ctx);
    }

    @Override
    public String getFluentEvent(FsmContext ctx) {
        if (null == ctx.getEvent()) {
            return Coasts.EVENT_FORWARD;
        }
        return ctx.getEvent();
    }


    public StatusManager getStatusManager(FsmContext ctx) {
        return InfraUtils.getStatusManager(ctx.getProfile().getStatusManager());
    }


    public void setFailover(FsmContext ctx, State failover) throws StatusException {

    }

    public boolean tryLock(FsmContext ctx) {
        return false;
    }

    public void unLock(FsmContext ctx) {

    }

    public Integer executeTimes(FsmContext ctx, State fail) {
        return null;
    }

    public void idempotent(String name, Serializable id, State state, String event) throws IdempotentException {
    }

    public void unidempotent(String name, Serializable id, State state, String event) throws IdempotentException {
    }
}


