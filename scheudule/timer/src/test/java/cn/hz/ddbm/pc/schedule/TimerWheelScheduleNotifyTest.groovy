package cn.hz.ddbm.pc.schedule

import cn.hz.ddbm.pc.core.support.ScheduleManger
import io.netty.util.HashedWheelTimer
import io.netty.util.Timeout
import io.netty.util.TimerTask
import org.junit.Test

import java.util.concurrent.TimeUnit

class TimerWheelScheduleNotifyTest {
    TimerWheelScheduleNotify timerWheelScheduleNotify = new TimerWheelScheduleNotify()

    @Test
    void testType() {
        ScheduleManger.Type result = timerWheelScheduleNotify.type()
        assert result == ScheduleManger.Type.TIMER
    }

    @Test
    void testSchedule() {
//        timerWheelScheduleNotify.schedule("flow", null)
    }

    @Test
    void testNotifyMe() {
//        timerWheelScheduleNotify.threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
//        timerWheelScheduleNotify.afterPropertiesSet()
//        timerWheelScheduleNotify.notifyMe("flow", new FsmPayload() {
//            @Override
//            Serializable getId() {
//                return 1
//            }
//
//            @Override
//            State getStatus() {
//                return  null;
//            }
//
//            @Override
//            void setStatus(State status) {
//
//            }
//        }, Coasts.EVENT_PAUSE,2, TimeUnit.NANOSECONDS)
//        Thread.sleep(2000)
    }
    private static final int NUM_TIMERS = 10; // 假设我们要创建100万个定时任务
    private static final int TIMEOUT_SECONDS = 10; // 超时时间设为10秒

    @Test
    public void test() {
        HashedWheelTimer timer = new HashedWheelTimer();

        for (int i = 0; i < NUM_TIMERS; i++) {
            int finalI = i;
            TimerTask task = new TimerTask() {
                @Override
                public void run(Timeout timeout) {
                    // 定时任务的逻辑
                    System.out.println(finalI + "，秒执行");
                }
            };
            // 启动定时任务，10秒后执行
            timer.newTimeout(task, i % 3, TimeUnit.SECONDS);
        }

        // 等待所有任务执行完毕，然后关闭定时器
        // 注意：实际压测时，可能需要更复杂的逻辑来确保所有任务都被执行
        // 例如，可以通过计数或其他同步机制来确保任务执行完毕
//        try {
//            Thread.sleep(4); // 假设等待10秒能够保证所有任务执行
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        timer.stop();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme