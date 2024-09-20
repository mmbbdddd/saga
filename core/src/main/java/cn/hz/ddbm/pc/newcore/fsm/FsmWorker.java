package cn.hz.ddbm.pc.newcore.fsm;


import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.fsm.actions.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.actions.RemoteFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.workers.FsmLocalWorker;
import cn.hz.ddbm.pc.newcore.fsm.workers.FsmRemoteWorker;
import lombok.Data;

@Data
public abstract class FsmWorker  {
    protected FsmFlow  fsm;
    protected Enum          state;
    protected Router  router;

    public FsmWorker(FsmFlow  fsm, Enum state, Router  router) {
        this.fsm    = fsm;
        this.state  = state;
        this.router = router;
    }

    public static   FsmWorker  local(FsmFlow  fsm, Enum from, Class<? extends LocalFsmAction> action, Router  router) {
        return new FsmLocalWorker(fsm,from, action, router);

    }

    public static   FsmWorker  remote(FsmFlow  fsm, Enum from, Class<? extends RemoteFsmAction> action, Router  router) {
        return new FsmRemoteWorker(fsm,from, action, router);
    }

    public abstract void execute(FlowContext<FsmState > ctx) throws Exception;

    public enum Offset {
        task, failover
    }
}
