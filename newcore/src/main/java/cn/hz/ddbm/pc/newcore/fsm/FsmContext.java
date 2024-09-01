package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Payload;

import java.util.Map;

public class FsmContext<S extends Enum<S>> extends FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> {


    public FsmContext(FsmFlow<S> flow, Payload<FsmState<S>> payload, Map<String, Object> session) {
        super(flow, payload, session);
    }
}
