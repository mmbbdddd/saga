package cn.hz.ddbm.pc.core;


import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.enums.FlowStatus;
import cn.hz.ddbm.pc.core.support.StatisticsSupport;
import cn.hz.ddbm.pc.core.utils.InfraUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

@Getter
public class FsmContext<S extends Enum<S>, T extends FsmPayload<S>> {
    //    入参
    private Serializable        id;
    private T                   data;
    private Fsm<S>              flow;
    @Setter
    private Boolean             fluent;
    @Setter
    private String              event;
    @Setter
    private FlowStatus          status;
    @Setter
    private S                   state;
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
    private Map<String, Object> session;


    public FsmContext(Fsm<S> flow, T data, String event, Profile profile) {
        Assert.notNull(flow, "flow is null");
        Assert.notNull(data, "data is null");
        Assert.notNull(event, "event is null");
        Assert.notNull(data.getId(), "date.id is null");
        Assert.notNull(data.getStatus(), "date.status is null");
        this.event   = event;
        this.data    = data;
        this.id      = data.getId();
        this.flow    = flow;
        this.status  = data.getStatus();
        this.state   = data.getState();
        this.fluent  = true;
        this.session = new HashMap<>();
        //todo需要从应用中同步
        if (flow.getProfile() != null) {
            this.profile = flow.getProfile();
        } else {
            this.profile = profile;
//            flow.profile = profile;
        }
    }


    public void metricsNode(FsmContext<S, ?> ctx) {
        StatisticsSupport metricsWindows = InfraUtils.getMetricsTemplate();
        ///todo 待优化。这块有比较大的IO，可以合并到session中。
        AtomicLong atomic = getSessionOrDefault(Coasts.EXECUTE_COUNT, new AtomicLong(0));
        atomic.incrementAndGet();
    }

    private <T> T getSessionOrDefault(String key, T deft) {
        return (T) session.computeIfAbsent(key, (Function<String, T>) s -> deft);
    }

    /**
     * 确保上下文状态一致
     * 1，status ==> entity
     */
    public void syncPayLoad() {
        data.setStatusSate(status,state);
    }

    public Map<String, Object> buildExpressionContext() {
        return null;
    }

    public void getSession(String key) {
        session.get(key);
    }

    public void setSession(String key, Object value) {
        session.put(key, value);
    }
}
