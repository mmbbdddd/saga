package cn.hz.ddbm.pc.core.processor.fsm;


import cn.hz.ddbm.pc.core.*;
import cn.hz.ddbm.pc.core.action.Action;
import cn.hz.ddbm.pc.core.action.CommandAction;
import cn.hz.ddbm.pc.core.action.QueryAction;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.exception.ActionException;
import cn.hz.ddbm.pc.core.exception.StatusException;

import java.io.Serializable;
import java.util.List;

/**
 * 业务一致性类业务通用流程
 * 1，设置状态位到容错位
 * 2，更新状态到状态管理器，如果成功则执行下一步
 * 3，条件执行业务逻辑
 * 4，执行成功+查询对方状态，更新状态
 * 5，更新成功，刷新状态
 * 6，异常，则充值状态位到容错（防45步骤错误）
 **/


public class FsmProcessor extends Processor {
    Transition transition;

    public FsmProcessor(Transition transition, List<Plugin> plugins) {
        super(plugins);
        this.transition = transition;
    }

    public Action getAction(FsmContext ctx) {
        return Actions.typeOf(this.transition.getActionDsl(), Action.class, ctx.getMockBean());
    }

    public void execute(FsmContext ctx) throws StatusException, ActionException {
        Fsm          flow     = ctx.getFlow();
        Serializable id       = ctx.getId();
        State        lastNode = ctx.getState();
        Action       action   = getAction(ctx);

        if (action instanceof CommandAction) {
            try {
                preActionPlugin(ctx);
                State to = ((CommandAction) action).execute(ctx);
                ctx.setState(to);
                postActionPlugin(lastNode, ctx);
            } catch (Exception e) {
                //有错误就重试，最大可能保证成功（但有可能重复执行，依赖对方冥等）
                ctx.setState(transition.getFrom());
                onActionExceptionPlugin(lastNode, e, ctx);
                throw new ActionException(e);
            } finally {
                onActionFinallyPlugin(ctx);
                ctx.metricsNode(ctx);
            }
        } else {
            try {
                preActionPlugin(ctx);
                State to = ((QueryAction) action).queryState(ctx);
                ctx.setState(to);
                postActionPlugin(lastNode, ctx);
            } catch (Exception e) {
//                //有错误就重试，最大可能保证成功（但有可能重复执行，依赖对方冥等）
//                ctx.setState(t.getFrom());
                onActionExceptionPlugin(lastNode, e, ctx);
                throw new ActionException(e);
            } finally {
                onActionFinallyPlugin(ctx);
                ctx.metricsNode(ctx);
            }
        }
    }

    @Override
    public String getFluentEvent(FsmContext ctx) {
        return Coasts.EVENT_DEFAULT;
    }


}


