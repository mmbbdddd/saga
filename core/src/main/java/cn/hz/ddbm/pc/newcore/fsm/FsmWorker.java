package cn.hz.ddbm.pc.newcore.fsm;


import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.fsm.actions.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.actions.RemoteFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.workers.FsmLocalWorker;
import cn.hz.ddbm.pc.newcore.fsm.workers.FsmRemoteWorker;
import lombok.Data;

@Data
public abstract class FsmWorker<S extends Enum<S>> {
    protected FsmFlow<S> fsm;
    protected S          state;
    protected Router<S> router;

    public FsmWorker(FsmFlow<S> fsm, S state, Router<S> router) {
        this.fsm    = fsm;
        this.state  = state;
        this.router = router;
    }

    public static <S extends Enum<S>> FsmWorker<S> local(FsmFlow<S> fsm, S from, Class<? extends LocalFsmAction> action, Router<S> router) {
        return new FsmLocalWorker<>(fsm,from, action, router);

    }

    public static <S extends Enum<S>> FsmWorker<S> remote(FsmFlow<S> fsm, S from, Class<? extends RemoteFsmAction> action, Router<S> router) {
        return new FsmRemoteWorker<>(fsm,from, action, router);
    }

    public abstract void execute(FsmContext<S> ctx) throws ActionException;

    public enum Offset {
        task, failover
    }
}
