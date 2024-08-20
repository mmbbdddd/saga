package cn.hz.ddbm.pc.core;


import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.support.StatisticsSupport;
import cn.hz.ddbm.pc.core.utils.InfraUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
public class FlowContext<S extends Enum<S>, T extends FlowPayload<S>> {
    //    入参
    private Serializable        id;
    private T                   data;
    private Fsm<S>              flow;
    @Setter
    private Boolean             fluent;
    @Setter
    private String              event;
    @Setter
    private State<S>            status;
    @Setter
    private BaseProcessor<?, S> executor;
    @Setter
    private Profile<S>          profile;
    @Setter
    private Boolean             mockBean = false;
    @Setter
    private Boolean             isChaos  = false;
    @Setter
    private S                   nextNode;


    public FlowContext(Fsm<S> flow, T data, String event, Profile profile) {
        Assert.notNull(flow, "flow is null");
        Assert.notNull(data, "data is null");
        Assert.notNull(event, "event is null");
        Assert.notNull(data.getId(), "date.id is null");
        Assert.notNull(data.getStatus(), "date.status is null");
        this.event  = event;
        this.data   = data;
        this.id     = data.getId();
        this.flow   = flow;
        this.status = data.getStatus();
        this.fluent = true;
        //todo需要从应用中同步
        if (flow.getProfile() != null) {
            this.profile = flow.getProfile();
        } else {
            this.profile = profile;
//            flow.profile = profile;
        }
    }


    public void metricsNode(FlowContext<S, ?> ctx) {
        StatisticsSupport metricsWindows = InfraUtils.getMetricsTemplate();
        metricsWindows.increment(ctx.getFlow().getName(), ctx.getId(), ctx.getStatus().getName(), Coasts.EXECUTE_COUNT);
    }

    /**
     * 确保上下文状态一致
     * 1，status ==> entity
     */
    public void syncPayLoad() {
        data.setStatus(status);
    }

    public Map<String, Object> buildExpressionContext() {
        return null;
    }
}
