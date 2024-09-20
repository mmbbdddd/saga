package cn.hz.ddbm.pc.newcore.saga.workers;


import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;

public class FailWorker extends SagaWorker {

    public FailWorker() {
        super(-1);
    }

    @Override
    public void execute(FlowContext<SagaState> ctx)   {
     }

}
