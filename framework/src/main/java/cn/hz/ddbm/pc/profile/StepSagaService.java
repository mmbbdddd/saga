package cn.hz.ddbm.pc.profile;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hz.ddbm.pc.core.*;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.exception.ActionException;
import cn.hz.ddbm.pc.core.exception.IdempotentException;
import cn.hz.ddbm.pc.core.exception.StatusException;

public class StepSagaService extends BaseService {
    public  void oneStep(String flowName, FsmPayload payload, String event) throws ActionException, FsmEndException, StatusException, IdempotentException {
        Assert.notNull(flowName, "flowName is null");
        Assert.notNull(payload, "FlowPayload is null");
        event = StrUtil.isBlank(event) ? Coasts.EVENT_FORWARD : event;
        Fsm            flow = getFlow(flowName);
        FsmContext  ctx  = new FsmContext(flow, payload, event, Profile.devOf());
        oneStep(ctx);
    }

    public void oneStep(FsmContext  ctx) throws ActionException, FsmEndException, StatusException, IdempotentException {
        Fsm     flow      = ctx.getFlow();
        Boolean rawFluent = ctx.getFluent();
        ctx.setFluent(false);
        flow.execute(ctx);
        ctx.setFluent(rawFluent);
    }

}
