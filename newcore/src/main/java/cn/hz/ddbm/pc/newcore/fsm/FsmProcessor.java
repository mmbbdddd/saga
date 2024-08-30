package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.FlowProcessorService;
import cn.hz.ddbm.pc.newcore.exception.FlowEndException;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.PauseException;

public class FsmProcessor extends FlowProcessorService<FsmContext> {


    @Override
    public void workerProcess(FsmContext ctx) throws FlowEndException, InterruptedException, PauseException {

    }
}
