package cn.hz.ddbm.pc.newcore;

import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.*;

public abstract class Worker<A extends Action, C extends FlowContext<?, ?, ?>> {
    public abstract void execute(C ctx) throws StatusException, IdempotentException, ActionException, LockException, PauseException, FlowEndException, InterruptedException, ProcessingException, NoSuchRecordException;
}
