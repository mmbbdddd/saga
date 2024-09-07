package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Payload;
import cn.hz.ddbm.pc.newcore.config.Coast;

import java.util.Map;
import java.util.Objects;

public class SagaContext<S extends Enum<S>> extends FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> {

    public SagaContext(SagaFlow<S> flow, Payload<SagaState<S>> payload, Map<String, Object> session) {
        super(flow, payload, session);
    }

    public String getEvent() {
        return getState().getDirection().isForward() ? Coast.SAGA.EVENT_FORWARD : Coast.SAGA.EVENT_BACKOFF;
    }


}
