package cn.hz.ddbm.pc;


import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

public class HashedWheelTimerPressureTest {

    private static final int NUM_TIMERS = 1000000000; // 假设我们要创建100万个定时任务
    private static final int TIMEOUT_SECONDS = 10; // 超时时间设为10秒

    public static void main(String[] args) {
        HashedWheelTimer timer = new HashedWheelTimer();

        for (int i = 0; i < NUM_TIMERS; i++) {
            TimerTask task = new TimerTask() {
                @Override
                public void run(Timeout timeout) {
                    // 定时任务的逻辑
                }
            };
            // 启动定时任务，10秒后执行
            timer.newTimeout(task, TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }

        // 等待所有任务执行完毕，然后关闭定时器
        // 注意：实际压测时，可能需要更复杂的逻辑来确保所有任务都被执行
        // 例如，可以通过计数或其他同步机制来确保任务执行完毕
        try {
            Thread.sleep(10000); // 假设等待10秒能够保证所有任务执行
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timer.stop();
    }
}