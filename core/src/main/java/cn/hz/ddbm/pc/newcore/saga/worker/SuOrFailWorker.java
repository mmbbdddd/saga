package cn.hz.ddbm.pc.newcore.saga.worker;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;

public class SuOrFailWorker extends SagaWorker {
    public SuOrFailWorker(Integer index) {
        super(index, null);
    }

    @Override
    public void execute(FlowContext ctx) throws IdempotentException, ActionException, LockException, FlowEndException, NoSuchRecordException {

    }
}
