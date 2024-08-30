package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Payload;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.config.Coast;

import java.io.Serializable;
import java.util.Map;

public class SagaContext<S> extends FlowContext<SagaModel<S>, SagaState<S>, SagaWorker<S>> {

    public SagaContext(SagaModel<S> flow, Serializable id, Payload<SagaState<S>> payload, Profile profile, Map<String, Object> session) {
        super(flow, id, payload, profile, session);
    }

    public String getEvent() {
        return getState().getIsForward() ? Coast.SAGA.EVENT_FORWARD : Coast.SAGA.EVENT_BACKOFF;
    }


    @Override
    public Integer getRetry(SagaState<S> state) {
        return null;
    }


}
