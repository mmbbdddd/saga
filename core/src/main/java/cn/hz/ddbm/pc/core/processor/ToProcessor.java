package cn.hz.ddbm.pc.core.processor;

import cn.hz.ddbm.pc.core.*;
import cn.hz.ddbm.pc.core.exception.wrap.ActionException;

import java.io.Serializable;
import java.util.List;

/**
 * 通用非一致性业务，也非查询类业务。
 * @param <S>
 */
public class ToProcessor<S extends Enum<S>> extends BaseProcessor<Action<S>, S> {


    public ToProcessor(Fsm.Transition<S> f, List<Plugin> plugins) {
        super(f, plugins);
    }

    @Override
    public Action<S> action(FsmContext<S, ?> ctx) {
        return Action.of(getFsmRecord().getActionDsl(), Action.class, ctx);
    }

    public void execute(FsmContext<S, ?> ctx) throws ActionException {
        Fsm<S>       flow     = ctx.getFlow();
        Serializable id       = ctx.getId();
        String       event    = ctx.getEvent();
        State<S>     lastNode = ctx.getStatus();
        try {
            preActionPlugin(flow, ctx);
            action(ctx).execute(ctx);
            ctx.getStatus().flush(event, getFsmRecord().getTo(), flow);
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
