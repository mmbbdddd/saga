package cn.hz.ddbm.pc.core.processor;

import cn.hz.ddbm.pc.core.*;
import cn.hz.ddbm.pc.core.action.SagaAction;
import cn.hz.ddbm.pc.core.exception.wrap.ActionException;

import java.io.Serializable;
import java.util.List;

/**
 * 通用非一致性业务，也非查询类业务。
 *
 * @param <S>
 */
public class ToProcessor<S extends Enum<S>> extends BaseProcessor<SagaAction<S>, S> {


    public ToProcessor(Fsm.Transition<S> f, List<Plugin> plugins) {
        super(f, plugins);
    }

    @Override
    public SagaAction<S> action(FsmContext<S, ?> ctx) {
        return Actions.typeOf(getFsmRecord(), SagaAction.class, ctx.getMockBean());
    }

    public void execute(FsmContext<S, ?> ctx) throws ActionException {
        Fsm<S>       flow     = ctx.getFlow();
        Serializable id       = ctx.getId();
        String       event    = ctx.getEvent();
        S            lastNode = ctx.getState();
        try {
            preActionPlugin(flow, ctx);
            action(ctx).execute(ctx);
            ctx.setState(getFsmRecord().getTo());
            postActionPlugin(flow, lastNode, ctx);
        } catch (Exception e) {
            ctx.setState(getFsmRecord().getFrom());
            onActionExceptionPlugin(flow, lastNode, e, ctx);
            throw new ActionException(e);
        } finally {
            onActionFinallyPlugin(flow, ctx);
            ctx.metricsNode(ctx);
        }
    }

}
