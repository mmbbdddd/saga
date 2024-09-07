package cn.hz.ddbm.pc.newcore.config;

public class Coast {
    public static final String  NONE_FSM_ACTION         = "noneFsmAction";
    public static final String  NONE_SAGA_ACTION        = "noneSagaAction";
    public static final String  LOCAL_CHAOS_ACTION      = "localChaosAction";
    public static final String  REMOTE_CHAOS_ACTION     = "remoteChaosAction";
    public static final String  PLUGIN_EXECUTOR_SERVICE = "pluginExecutorService";
    public static final String  ACTION_EXECUTOR_SERVICE = "actionExecutorService";
    public static final String  RUN_MODE                = "run_mode";
    public static final String  RUN_MODE_CHAOS          = "chaos";
    public static final String  RUN_MODE_STABLE         = "stable";
    public static final String  CHAOS_WEIGHTS           = "chaos_weight";
    public static       Integer DEFAULT_RETRYTIME       = 1;

    public static class STATISTICS {
        public final static String EXECUTE_TIMES = "execute_times";

    }

    public static class SAGA {
        public final static String EVENT_FORWARD = "forward";
        public final static String EVENT_BACKOFF = "backoff";

    }

    public static class FSM {
        public final static String EVENT_DEFAULT = "push";
    }

    public enum LockType {
        jvm,
        zk,
        redis,
        db,
    }

    public enum ScheduleType {
        //        定时调度
        spring_cron,
        xxl,
        //        精确调度
        timer,
        delay_queue
    }

    public enum SessionType {
        jvm,
        redis
    }

    public enum StatusType {
        jvm,
        redis,
        dao
    }

    public enum StatisticsType {
        jvm,
        redis
    }
}
