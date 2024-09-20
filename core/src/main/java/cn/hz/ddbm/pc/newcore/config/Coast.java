package cn.hz.ddbm.pc.newcore.config;

public class Coast {
    public static final String  NONE_FSM_ACTION         = "noneFsmAction";
    public static final String  NONE_SAGA_ACTION        = "noneSagaAction";
    public static final String  LOCAL_CHAOS_ACTION      = "localChaosAction";
    public static final String  REMOTE_CHAOS_ACTION     = "remoteChaosAction";
    public static final String  PLUGIN_EXECUTOR_SERVICE = "pluginExecutorService";
    public static final String  ACTION_EXECUTOR_SERVICE = "actionExecutorService";
    public static final String  CHAOS_WEIGHTS           = "chaos_weight";
    public static       Integer DEFAULT_RETRYTIME       = 1;

    public final static String EVENT_DEFAULT = "push";

    public static class STATISTICS {
        public final static String EXECUTE_TIMES = "execute_times";

    }

    public static class SAGA {
        public final static String CHAOS_MODE         = "saga_mode";
        public final static String CHAOS_TRUE         = "true";
        public final static String CHAOS_FALSE        = "false";
        public final static String CHAOS_RANDOM       = "random";
        public final static Double CHAOS_TRUE_WEIGHT  = 0.9;
        public final static Double CHAOS_FALSE_WEIGHT = 0.1;

    }


    public static class FSM {
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
