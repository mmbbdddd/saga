package cn.hz.ddbm.pc.core.processor;

import cn.hz.ddbm.pc.core.*;
import cn.hz.ddbm.pc.core.exception.wrap.ActionException;

import java.io.Serializable;
import java.util.List;

public class RouterProcessor<S extends Enum<S>> extends BaseProcessor<Action.QueryAction<S>, S> {

    public RouterProcessor(Fsm.FsmRecord<S> f, List<Plugin> plugins) {
        super(f, plugins);
    }


    @Override
    public Action.QueryAction<S> action(FlowContext<S, ?> ctx) {
        return Action.of(getFsmRecord().getActionDsl(), Action.QueryAction.class, ctx);
    }


    public void execute(FlowContext<S, ?> ctx) throws ActionException {
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
            postActionPlugin(flow, lastNode.getName(), ctx);
        } catch (Exception e) {
            ctx.getStatus().flush(event, getFsmRecord().getFrom(), flow);
            onActionExceptionPlugin(flow, lastNode.getName(), e, ctx);
            throw new ActionException(e);
        } finally {
            onActionFinallyPlugin(flow, ctx);
            ctx.metricsNode(ctx);
        }
    }


}
