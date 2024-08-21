package cn.hz.ddbm.pc.core.schedule;

import cn.hz.ddbm.pc.core.FlowPayload;
import cn.hz.ddbm.pc.core.FsmPayload;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public interface ScheduleManger {
    Type type();

    /**
     * 批量调度（定时，spring-cron，xxljob）
     *
     * @param flow
     * @param state
     */
    void schedule(String flow, Enum state, String event);

    /**
     * 精确调度（实时，延迟：时间轮，业务回调，延迟队列等0
     *
     * @param flow
     * @param payload
     * @param delay
     * @param timeUnit
     */
    void notifyMe(String flow, FsmPayload<?> payload , String event, Integer delay, TimeUnit timeUnit);

    enum Type {
        //        定时调度
        SPRING_CRON, XXL,

        //        精确调度
        TIMER, NOTIFY, DELAY_QUEUE
    }
}
