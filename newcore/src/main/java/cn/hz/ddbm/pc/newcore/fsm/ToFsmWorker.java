package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.FlowProcessorService;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.StatusException;

import java.io.Serializable;

public class ToFsmWorker<S extends Serializable> extends FsmWorker<S> implements FsmCommandAction<S> {

    FsmState<S>           from;
    FsmState<S>           to;
    String                action;
    FsmCommandActionProxy commandAction;

    public ToFsmWorker(S from, String commandAction, S to) {
        this.from          = new FsmState<>(from);
        this.action        = commandAction;
        this.to            = new FsmState<>(to);
        this.commandAction = new FsmCommandActionProxy(this.action);
    }

    @Override
    public void execute(FsmContext<S> ctx) throws StatusException, ActionException {
        FlowProcessorService processor = ctx.getProcessor();
        FsmModel<S>          flow      = ctx.getFlow();
        Serializable         id        = ctx.getId();
        String               event     = ctx.getEvent();
        FsmState<S>          lastNode  = ctx.getState();
        ctx.setAction(this.commandAction.getOrInitAction());
        try {
            processor.plugin().pre(ctx);
            commandAction.execute(ctx);
            ctx.setState(to);
            processor.plugin().post(lastNode, ctx);
        } catch (ActionException e) {
            processor.plugin().error(lastNode, e, ctx);
            throw e;
        } finally {
            processor.plugin()._finally(ctx);
            ctx.metricsNode(ctx);
        }
    }


    @Override
    public String code() {
        return commandAction.code();
    }


}