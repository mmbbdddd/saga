package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.Worker;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.router.LocalRouter;
import cn.hz.ddbm.pc.newcore.fsm.router.LocalToRouter;
import cn.hz.ddbm.pc.newcore.fsm.router.RemoteRouter;
import cn.hz.ddbm.pc.newcore.fsm.worker.FsmLocalWorker;
import cn.hz.ddbm.pc.newcore.fsm.worker.FsmRemoteWorker;
import lombok.Data;

@Data
public abstract class FsmWorker<S extends Enum<S>> extends Worker<FsmContext<S>> {
    public static <S extends Enum<S>> FsmWorker<S> local(S from, Class<? extends LocalFsmAction> action, LocalRouter<S> router) {
        return new FsmLocalWorker<>(from, action, router);

    }

    public static <S extends Enum<S>> FsmWorker<S> remote(S from, Class<? extends RemoteFsmAction> action, RemoteRouter<S> router) {
        return new FsmRemoteWorker<>(from, action, router);
    }

    public abstract void execute(FsmContext<S> ctx) throws StatusException, IdempotentException, ActionException, LockException, PauseException, FlowEndException, InterruptedException, ProcessingException, NoSuchRecordException;
}




