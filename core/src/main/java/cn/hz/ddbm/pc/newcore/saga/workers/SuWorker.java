package cn.hz.ddbm.pc.newcore.saga.workers;


import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;

public class SuWorker extends SagaWorker {


    public SuWorker(Integer index) {
        super(index);
    }

    @Override
    public void execute(FlowContext<SagaState> ctx)   {
     }

}
