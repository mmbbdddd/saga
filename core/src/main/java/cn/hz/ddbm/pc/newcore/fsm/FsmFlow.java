package cn.hz.ddbm.pc.newcore.fsm;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import cn.hz.ddbm.pc.newcore.BaseFlow;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.fsm.actions.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.actions.RemoteFsmAction;
import cn.hz.ddbm.pc.newcore.log.Logs;

import java.util.Objects;

public class FsmFlow<S extends Enum<S>> extends BaseFlow<FsmState<S>> {
    Table<S, String, FsmWorker<S>> eventTables;
    S                              init;
    S                              su;
    S                              fail;

    public FsmFlow(S init, S su, S fail) {
        this.init        = init;
        this.su          = su;
        this.fail        = fail;
        this.eventTables = new RowKeyTable<>();
    }


    public void execute(FsmContext<S> ctx) throws ActionException {
        Assert.notNull(ctx, "ctx is null");
        Assert.notNull(ctx.state, "ctx.state is null");
        Assert.notNull(ctx.state.flowStatus, "ctx.flowstatus is null");
        Assert.notNull(ctx.state.state, "ctx.state is null");
        Assert.notNull(ctx.state.offset, "ctx.offset is null");
        Logs.flow.info("{}", ctx.state.state);
        if (isFail(ctx.state.state)) {
            ctx.state.flowStatus = (FlowStatus.FAIL);
            return;
        }
        if (isSu(ctx.state.state)) {
            ctx.state.flowStatus = (FlowStatus.SU);
            return;
        }
        FsmWorker<S> worker = getWorker(ctx.state.state, ctx.event);
        if (!ctx.state.flowStatus.equals(FlowStatus.RUNNABLE)) {
            return;
        }
        worker.execute(ctx);
        if (ctx.getFluent()) {
            execute(ctx);
        }
    }

    private boolean isSu(S state) {
        return Objects.equals(su, state);
    }

    private boolean isFail(S state) {
        return Objects.equals(fail, state);
    }

    private FsmWorker<S> getWorker(S state, String event) {
        return eventTables.get(state, event);
    }

    public FsmFlow<S> local(S from, String event, Class<? extends LocalFsmAction> action, Router<S> router) {
        FsmWorker<S> sagaFsmWorker = FsmWorker.local(this, from, action, router);
        this.eventTables.put(from, event, sagaFsmWorker);
        return this;
    }

    public FsmFlow<S> remote(S from, String event, Class<? extends RemoteFsmAction> action, Router<S> router) {
        FsmWorker<S> sagaFsmWorker = FsmWorker.remote(this, from, action, router);
        this.eventTables.put(from, event, sagaFsmWorker);
        return this;
    }
}
