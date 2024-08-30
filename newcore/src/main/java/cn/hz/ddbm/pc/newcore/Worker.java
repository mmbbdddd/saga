package cn.hz.ddbm.pc.newcore;

import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.StatusException;

public abstract class Worker<C extends FlowContext> {
    public abstract void execute(C ctx) throws StatusException, IdempotentException, ActionException;
}
