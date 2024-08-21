package cn.hz.ddbm.pc.schedule;

import cn.hz.ddbm.pc.core.schedule.ScheduleManger;
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
    private HashedWheelTimer timer;
    @Autowired
    ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Override
    public Type type() {
        return Type.TIMER;
    }

    @Override
    public void schedule(String flow, Enum state, String event) {
        throw new NotImplementedException();
    }

    @Override
    public void notifyMe(String flow, Serializable flowId, String event, Integer delay, TimeUnit timeUnit) {
        timer.newTimeout(new FsmTimerTask(flow, flowId, event), delay, timeUnit);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        timer = new HashedWheelTimer(threadPoolTaskScheduler, 2L, TimeUnit.SECONDS, 2);
    }


    static class FsmTimerTask implements TimerTask {
        String       flowName;
        Serializable flowId;
        String       event;

        public FsmTimerTask(String flowName, Serializable flowId, String event) {
            this.flowName = flowName;
            this.flowId   = flowId;
            this.event    = event;
        }

        @Override
        public void run(Timeout timeout) throws Exception {

        }

        public String getFlowName() {
            return flowName;
        }

        public Serializable getFlowId() {
            return flowId;
        }

        public String getEvent() {
            return event;
        }
    }

}
