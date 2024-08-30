package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Payload;
import cn.hz.ddbm.pc.newcore.Profile;

import java.util.Map;

public class FsmContext extends FlowContext<FsmModel, FsmState, FsmWorker> {

    public FsmContext(FsmModel flow, Payload<FsmState> payload, Profile profile, Map<String, Object> session) {
        super(flow, payload, profile, session);
    }

    @Override
    public Integer getRetry(FsmState state) {
        return null;
    }
}
