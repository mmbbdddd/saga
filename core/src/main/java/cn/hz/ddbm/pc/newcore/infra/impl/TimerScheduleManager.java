package cn.hz.ddbm.pc.newcore.infra.impl;

import cn.hz.ddbm.pc.newcore.Payload;
import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.infra.ScheduleManger;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class TimerScheduleManager implements ScheduleManger {
    @Resource
    ThreadFactory threadPoolTaskScheduler;

    HashedWheelTimer timer;


    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        timer = new HashedWheelTimer(threadPoolTaskScheduler, 2L, TimeUnit.SECONDS, 2);
    }

    @Override
    public Coast.ScheduleType code() {
        return Coast.ScheduleType.timer;
    }

    @Override
    public void schedule(String flow, State state, String event) {
        //todo null;
        throw new RuntimeException("未实现异常");
    }

    @Override
    public void notifyMe(String flow, Payload payload, String event, Integer delay, TimeUnit timeUnit) {
        timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
//                pcService.execute(flow, payload, event);
            }
        }, delay, timeUnit);
    }
}
