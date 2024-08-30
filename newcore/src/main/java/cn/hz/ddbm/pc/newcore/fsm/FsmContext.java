package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Payload;
import cn.hz.ddbm.pc.newcore.Profile;

import java.io.Serializable;
import java.util.Map;

public class FsmContext<S extends Serializable> extends FlowContext<FsmModel<S>, FsmState<S>, FsmWorker<S>> {

    public FsmContext(FsmModel<S> flow, Payload<FsmState<S>> payload, Profile profile, Map<String, Object> session) {
        super(flow, payload, profile, session);
    }

    @Override
    public Integer getRetry(FsmState<S> state) {
        return null;
    }
}
