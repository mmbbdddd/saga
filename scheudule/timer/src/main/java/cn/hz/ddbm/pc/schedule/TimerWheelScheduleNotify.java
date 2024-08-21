package cn.hz.ddbm.pc.schedule;

import cn.hz.ddbm.pc.core.FlowPayload;
import cn.hz.ddbm.pc.core.FsmPayload;
import cn.hz.ddbm.pc.core.schedule.ScheduleManger;
import cn.hz.ddbm.pc.profile.StablePcService;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class TimerWheelScheduleNotify implements ScheduleManger, InitializingBean {

    @Autowired
    ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    StablePcService pcService;

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
        timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                pcService.execute(flow, payload, event);
            }
        }, delay, timeUnit);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        timer = new HashedWheelTimer(threadPoolTaskScheduler, 2L, TimeUnit.SECONDS, 2);
    }


}
