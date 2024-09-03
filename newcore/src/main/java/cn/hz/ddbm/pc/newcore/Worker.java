package cn.hz.ddbm.pc.newcore;

import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;

public abstract class Worker<C extends FlowContext> {
    public abstract void execute(C ctx) throws StatusException, IdempotentException, ActionException, LockException, PauseException, FlowEndException, InterruptedException;
}
