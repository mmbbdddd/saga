package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Payload;
import cn.hz.ddbm.pc.newcore.Profile;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

public class FsmContext<S extends Serializable> extends FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> {

    String event;

    public FsmContext(FsmFlow<S> flow,String event, Payload<FsmState<S>> payload, Profile profile, Map<String, Object> session) {
        super(flow, payload, profile, session);
        this.event = event;
    }




    public String getEvent() {
        return event;
    }
}
