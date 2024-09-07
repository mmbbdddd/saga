package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Payload;
import cn.hz.ddbm.pc.newcore.fsm.router.LocalRouter;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class FsmContext<S extends Enum<S>> extends FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> {
    @Setter
    @Getter
    FsmRouter<S> router;

    public FsmContext(FsmFlow<S> flow, Payload<FsmState<S>,FsmFlow<S>> payload, Map<String, Object> session) {
        super(flow, payload, session);
    }


}
