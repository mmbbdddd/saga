package cn.hz.ddbm.pc.core.processor;

import cn.hz.ddbm.pc.core.*;
import cn.hz.ddbm.pc.core.action.QueryAction;
import cn.hz.ddbm.pc.core.exception.wrap.ActionException;

import java.io.Serializable;
import java.util.List;

/**
 * 查询类接口通用流程
 *
 * @param <S>
 */
public class RouterProcessor<S extends Enum<S>> extends BaseProcessor<QueryAction<S>, S> {

    public RouterProcessor(Fsm.Transition<S> f, List<Plugin> plugins) {
        super(f, plugins);
    }


    @Override
    public QueryAction<S> action(FsmContext<S, ?> ctx) {
        return Actions.of(getFsmRecord(), QueryAction.class, ctx.getMockBean());
    }


    public void execute(FsmContext<S, ?> ctx) throws ActionException {
        Fsm<S>       flow     = ctx.getFlow();
        Serializable id       = ctx.getId();
        String       event    = ctx.getEvent();
        State<S>     lastNode = ctx.getStatus();
        try {
            preActionPlugin(flow, ctx);
            S nextNode = action(ctx).query(ctx);
            if (null == nextNode) {
                nextNode = getFsmRecord().getFrom();
            }
            ctx.getStatus().flush(event, nextNode, flow);
            postActionPlugin(flow, lastNode.getState(), ctx);
        } catch (Exception e) {
            ctx.getStatus().flush(event, getFsmRecord().getFrom(), flow);
            onActionExceptionPlugin(flow, lastNode.getState(), e, ctx);
            throw new ActionException(e);
        } finally {
            onActionFinallyPlugin(flow, ctx);
            ctx.metricsNode(ctx);
        }
    }


}
