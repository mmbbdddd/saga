package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.Worker;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.StatusException;

public class FsmWorker extends Worker<FsmContext> {
    @Override
    public void execute(FsmContext ctx) throws StatusException, IdempotentException, ActionException {

    }

    public FsmActionProxy getAction() {
        return null;
    }
}
