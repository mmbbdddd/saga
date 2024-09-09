package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Worker;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.worker.FsmLocalWorker;
import cn.hz.ddbm.pc.newcore.fsm.worker.FsmRemoteWorker;
import lombok.Data;

@Data
public abstract class FsmWorker<E extends Enum<E>> extends Worker<FsmAction, FlowContext<FsmFlow<E>, FsmState<E>, FsmWorker<E>>> {
    public static <S extends Enum<S>> FsmWorker<S> local(S from, Class<? extends LocalFsmAction> action, Router<S> router) {
        return new FsmLocalWorker<>(action, router);

    }

    public static <S extends Enum<S>> FsmWorker<S> remote(S from, Class<? extends RemoteFsmAction> action, Router<S> router) {
        return new FsmRemoteWorker<>(action, router);
    }

    public abstract void execute(FlowContext<FsmFlow<E>, FsmState<E>, FsmWorker<E>> ctx) throws StatusException, IdempotentException, ActionException, LockException, PauseException, FlowEndException, InterruptedException, ProcessingException, NoSuchRecordException;
}




