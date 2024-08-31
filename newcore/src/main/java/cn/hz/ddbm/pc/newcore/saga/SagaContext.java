package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Payload;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.config.Coast;

import java.util.Map;

public class SagaContext<S> extends FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> {

    public SagaContext(SagaFlow<S> flow, Payload<SagaState<S>> payload, Profile profile, Map<String, Object> session) {
        super(flow, payload, profile, session);
    }

    public String getEvent() {
        return getState().getIsForward() ? Coast.SAGA.EVENT_FORWARD : Coast.SAGA.EVENT_BACKOFF;
    }


}
