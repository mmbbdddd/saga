package cn.hz.ddbm.pc.newcore.infra;

import cn.hz.ddbm.pc.newcore.Payload;
import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.config.Coast;

import java.util.concurrent.TimeUnit;

/**
 * 调度接口
 */
public interface ScheduleManger {
    Coast.ScheduleType code();

    /**
     * 批量调度（定时，spring-cron，xxljob）
     *
     * @param flow
     * @param state
     */
    void schedule(String flow, State state, String event);

    /**
     * 精确调度（实时，延迟：时间轮，业务回调，延迟队列等0
     *
     * @param flow
     * @param payload
     * @param delay
     * @param timeUnit
     */
    void notifyMe(String flow, Payload payload, String event, Integer delay, TimeUnit timeUnit);


}
