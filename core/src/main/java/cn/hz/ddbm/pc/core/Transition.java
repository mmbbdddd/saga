package cn.hz.ddbm.pc.core;

import cn.hz.ddbm.pc.core.action.Action;
import cn.hz.ddbm.pc.core.exception.ActionException;
import cn.hz.ddbm.pc.core.exception.wrap.StatusException;
import cn.hz.ddbm.pc.core.processor.RouterProcessor;
import cn.hz.ddbm.pc.core.processor.SagaProcessor;
import cn.hz.ddbm.pc.core.processor.ToProcessor;
import lombok.Data;

import java.util.Set;

@Data
public class Transition<S extends Enum<S>> {
    Type                type;
    S                   from;
    String              event;
    String              actionDsl;
    S                   to;
    S                   failover;
    Set<S>              conditions;
    BaseProcessor<?, S> processor;

    public static <S extends Enum<S>> Transition<S> queryOf(S node, String event, String action) {
        Transition<S> t = new Transition<S>();
        t.type      = Type.QUERY;
        t.from      = node;
        t.event     = event;
        t.actionDsl = action;
        return t;
    }

    public static <S extends Enum<S>> Transition<S> toOf(S from, String event, String action, S to) {
        Transition<S> t = new Transition<S>();
        t.type      = Type.TO;
        t.from      = from;
        t.event     = event;
        t.actionDsl = action;
        t.to        = to;
        return t;
    }

    public static <S extends Enum<S>> Transition<S> sagaOf(S node, String event, S failover, String action) {
        Transition<S> t = new Transition<S>();
        t.type       = Type.SAGA;
        t.from       = node;
        t.event      = event;
        t.actionDsl  = action;
        t.failover   = failover;
        return t;
    }


    public void execute(FsmContext<S, ?> ctx) throws StatusException, ActionException {
        _getProcessor(ctx).execute(ctx);
    }

    public void interruptedPlugins(FsmContext<S, ?> ctx) {
        _getProcessor(ctx).interrupteFlowForPlugins(ctx);
    }

    private <A extends Action<S>> BaseProcessor<?, S> _getProcessor(FsmContext<S, ?> ctx) {
        if (null == processor) {
            synchronized (this) {
                switch (type) {
                    case TO: {
                        this.processor = new ToProcessor<S>(this, ctx.getProfile().getPlugins());
                        break;
                    }
                    case SAGA: {
                        this.processor = new SagaProcessor<S>(this, ctx.getProfile().getPlugins());
                        break;
                    }
                    default: {
                        this.processor = new RouterProcessor<S>(this, ctx.getProfile().getPlugins());
                        break;
                    }
                }
            }
        }
        return this.processor;
    }


    public enum Type {
        TO, SAGA, QUERY
    }
}
