package cn.hz.ddbm.pc.newcore.saga.workers;


import cn.hz.ddbm.pc.newcore.exception.FlowEndException;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;

public class FailWorker extends SagaWorker {

    public FailWorker() {
        super(-1);
    }

    @Override
    public void execute(SagaContext ctx)   {
     }

    @Override
    public boolean isFail() {
        return true;
    }

    @Override
    public boolean isSu() {
        return false;
    }
}
