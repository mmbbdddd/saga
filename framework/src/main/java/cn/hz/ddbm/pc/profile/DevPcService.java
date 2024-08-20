package cn.hz.ddbm.pc.profile;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hz.ddbm.pc.core.*;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.exception.wrap.ActionException;
import cn.hz.ddbm.pc.core.exception.wrap.StatusException;

public class DevPcService extends PcService {
    public <S extends Enum<S>, T extends FsmPayload<S>> void oneStep(String flowName, T payload, String event) throws ActionException, FsmEndException, StatusException {
        Assert.notNull(flowName, "flowName is null");
        Assert.notNull(payload, "FlowPayload is null");
        event = StrUtil.isBlank(event) ? Coasts.EVENT_DEFAULT : event;
        Fsm<S>           flow = getFlow(flowName);
        FsmContext<S, T> ctx  = new FsmContext<>(flow, payload, event, Profile.devOf());
        oneStep(ctx);
    }

    public void oneStep(FsmContext<?, ?> ctx) throws ActionException, FsmEndException, StatusException {
        Fsm     flow      = ctx.getFlow();
        Boolean rawFluent = ctx.getFluent();
        ctx.setFluent(false);
        flow.execute(ctx);
        ctx.setFluent(rawFluent);
    }

}
