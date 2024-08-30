package cn.hz.ddbm.pc.newcore.config;

public class Coast {
    public static final String PLUGIN_EXECUTOR_SERVICE = "pluginExecutorService";
    public static final String ACTION_EXECUTOR_SERVICE = "actionExecutorService";

    public static class SAGA {
        public final static String EVENT_FORWARD = "forward";
        public final static String EVENT_BACKOFF = "backoff";

    }

    public static class FSM {
        public final static String EVENT_DEFAULT = "push";
    }

    public enum ScheduleType {
        //        定时调度
        SPRING_CRON, XXL,

        //        精确调度
        TIMER, NOTIFY, DELAY_QUEUE
    }

    public enum SessionType {
        memory, redis
    }

    public enum StatusType {
        memory, redis, dao
    }

    public enum StatisticsType {
        WINDOWS, RANGE
    }
}
