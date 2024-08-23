package cn.hz.ddbm.pc.schedule;

import cn.hz.ddbm.pc.core.FsmPayload;
import cn.hz.ddbm.pc.core.exception.NotImplementedException;
import cn.hz.ddbm.pc.core.support.ScheduleManger;
import cn.hz.ddbm.pc.profile.Sagaervice;
import io.netty.util.HashedWheelTimer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class TimerWheelScheduleNotify implements ScheduleManger {

    @Resource
    ThreadFactory threadPoolTaskScheduler;

    @Resource
    Sagaervice pcService;

    HashedWheelTimer timer;

    @Override
    public Type type() {
        return Type.TIMER;
    }

    @Override
    public void schedule(String flow, Enum state, String event) {
        throw new NotImplementedException();
    }

    @Override
    public void notifyMe(String flow, FsmPayload payload, String event, Integer delay, TimeUnit timeUnit) {
        timer.newTimeout(timeout -> pcService.execute(flow, payload, event), delay, timeUnit);
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        timer = new HashedWheelTimer(threadPoolTaskScheduler, 2L, TimeUnit.SECONDS, 2);
    }


}
