package cn.hz.ddbm.pc.newcore.infra.proxy;

import cn.hz.ddbm.pc.newcore.Payload;
import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.infra.ScheduleManger;

import java.util.concurrent.TimeUnit;

public class ScheduleMangerProxy implements ScheduleManger {
    ScheduleManger scheduleManger;

    public ScheduleMangerProxy(ScheduleManger bean) {
        this.scheduleManger = bean;
    }

    @Override
    public Coast.ScheduleType code() {
        return scheduleManger.code();
    }

    @Override
    public void schedule(String flow, State state, String event) {
        try {
            scheduleManger.schedule(flow, state, event);
        } catch (Exception e) {
        }
    }

    @Override
    public void notifyMe(String flow, Payload payload, String event, Integer delay, TimeUnit timeUnit) {
        try {
            scheduleManger.notifyMe(flow, payload, event, delay, timeUnit);
        } catch (Exception e) {
        }
    }

}
